#!/usr/bin/env python3
"""Build, name and verify SudokuNova release assets without external Python packages.

The script never signs Android/Apple binaries. Signing and signature validation belong
to the protected CI steps. DEBUG APKs and iOS frameworks are explicitly labelled.
"""

from __future__ import annotations

import argparse
import hashlib
from pathlib import Path
import re
import shutil
import sys
from zipfile import ZIP_DEFLATED, BadZipFile, ZipFile

from scripts.verify_release_contract import (
    parse_desktop_package_version,
    parse_gradle_release_contract,
)

TAG_RE = re.compile(r"^v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$")

# One and only one output per requested build kind.
FILE_INPUTS = {
    "android-debug": ("app/build/outputs/apk/debug", "*.apk", "android-DEBUG.apk"),
    "android-signed": ("app/build/outputs/apk/release", "*.apk", "android-signed.apk"),
    "android-aab": ("app/build/outputs/bundle/release", "*.aab", "android-signed.aab"),
    "windows": ("sharedUI/build/compose/binaries/main/msi", "*.msi", "windows.msi"),
    "macos": ("sharedUI/build/compose/binaries/main/dmg", "*.dmg", "macos.dmg"),
    "linux": ("sharedUI/build/compose/binaries/main/deb", "*.deb", "linux.deb"),
}
ARCHIVE_INPUTS = {
    "web": ("sharedUI/build/dist/wasmJs/productionExecutable", "web.zip"),
    "ios-simulator": ("sharedUI/build/bin/iosSimulatorArm64/debugFramework", "ios-simulator-framework.zip"),
}
REQUIRED_SUFFIXES = (
    "android-DEBUG.apk", "windows.msi", "macos.dmg",
    "linux.deb", "web.zip", "ios-simulator-framework.zip",
)
OPTIONAL_SUFFIXES = ("android-signed.apk", "android-signed.aab")


def check_tag(tag: str, root: Path) -> str:
    match = TAG_RE.fullmatch(tag)
    if match is None:
        raise ValueError(f"Not a stable semantic version tag: {tag!r}")
    source = parse_gradle_release_contract(
        (root / "app/build.gradle.kts").read_text(encoding="utf-8")
    )
    desktop = parse_desktop_package_version(
        (root / "sharedUI/build.gradle.kts").read_text(encoding="utf-8")
    )
    expected = tag.removeprefix("v")
    if source.version_name != expected or desktop != expected:
        raise ValueError(
            f"Tag {tag} does not match Android {source.version_name} "
            f"and Desktop {desktop}"
        )
    return expected


def _exactly_one_file(directory: Path, pattern: str, kind: str) -> Path:
    if not directory.is_dir():
        raise ValueError(f"{kind}: output directory does not exist: {directory}")
    matches = sorted(
        p for p in directory.glob(pattern)
        if p.is_file() and not p.is_symlink() and "unsigned" not in p.name.lower()
    )
    if len(matches) != 1:
        raise ValueError(f"{kind}: expected exactly one packaged file, got {len(matches)}")
    if matches[0].stat().st_size == 0:
        raise ValueError(f"{kind}: packaged file is empty: {matches[0]}")
    return matches[0]


def _zip_directory(directory: Path, destination: Path, *, kind: str) -> None:
    if not directory.is_dir():
        raise ValueError(f"{kind}: source directory does not exist: {directory}")
    if kind == "web" and not (directory / "index.html").is_file():
        raise ValueError("web: production distribution has no index.html")
    if kind == "ios-simulator" and not (directory / "SudokuNovaSharedUI.framework").is_dir():
        raise ValueError("ios-simulator: expected simulator framework is missing")
    files = sorted(p for p in directory.rglob("*") if p.is_file())
    if not files:
        raise ValueError(f"{kind}: cannot package an empty directory")
    for path in files:
        if path.is_symlink() or not path.resolve().is_relative_to(directory.resolve()):
            raise ValueError(f"{kind}: unsafe symlink or external file: {path}")
    with ZipFile(destination, "w", compression=ZIP_DEFLATED, compresslevel=6) as archive:
        for path in files:
            archive.write(path, path.relative_to(directory).as_posix())


def stage_asset(kind: str, tag: str, root: Path, output: Path) -> Path:
    check_tag(tag, root)
    if kind not in FILE_INPUTS and kind not in ARCHIVE_INPUTS:
        raise ValueError(f"Unsupported platform asset kind: {kind}")
    output.mkdir(parents=True, exist_ok=True)
    if kind in FILE_INPUTS:
        folder, pattern, suffix = FILE_INPUTS[kind]
        source = _exactly_one_file(root / folder, pattern, kind)
        destination = output / f"SudokuNova-{tag}-{suffix}"
        shutil.copy2(source, destination)
    else:
        folder, suffix = ARCHIVE_INPUTS[kind]
        destination = output / f"SudokuNova-{tag}-{suffix}"
        _zip_directory(root / folder, destination, kind=kind)
    validate_binary(destination)
    return destination


def validate_binary(path: Path) -> None:
    if path.is_symlink() or not path.is_file() or path.stat().st_size == 0:
        raise ValueError(f"Missing, empty or linked release asset: {path}")
    suffix = path.suffix.lower()
    if suffix in {".apk", ".aab", ".zip"}:
        try:
            with ZipFile(path) as archive:
                if not archive.namelist() or archive.testzip() is not None:
                    raise ValueError(f"Invalid archive contents: {path.name}")
                if path.name.endswith("-web.zip") and "index.html" not in archive.namelist():
                    raise ValueError("Web asset has no root index.html")
        except BadZipFile as error:
            raise ValueError(f"Invalid ZIP-based asset: {path.name}") from error
    elif suffix in {".deb", ".msi"}:
        with path.open("rb") as stream:
            signature = stream.read(8)
        expected = b"!<arch>\n" if suffix == ".deb" else bytes.fromhex("d0cf11e0a1b11ae1")
        if signature != expected:
            raise ValueError(f"Invalid installer package header: {path.name}")
    elif suffix != ".dmg":
        raise ValueError(f"Unexpected release extension: {suffix}")


def create_checksum_manifest(tag: str, assets_dir: Path) -> Path:
    if TAG_RE.fullmatch(tag) is None:
        raise ValueError("Invalid release tag")
    assets = sorted(assets_dir.glob("*"))
    if any(not a.is_file() or a.is_symlink() for a in assets):
        raise ValueError("Unexpected directory or link in release assets")
    names = {asset.name for asset in assets}
    expected = {f"SudokuNova-{tag}-{suffix}" for suffix in REQUIRED_SUFFIXES}
    optional = {f"SudokuNova-{tag}-{suffix}" for suffix in OPTIONAL_SUFFIXES}
    missing = expected - names
    extra = names - expected - optional
    if missing or extra:
        raise ValueError(f"Incomplete/unknown assets: missing={sorted(missing)} extra={sorted(extra)}")
    signed_apk = f"SudokuNova-{tag}-android-signed.apk" in names
    signed_aab = f"SudokuNova-{tag}-android-signed.aab" in names
    if signed_apk != signed_aab:
        raise ValueError("Signed APK and AAB must be published together")
    lines = [f"# SudokuNova {tag} SHA-256 checksums"]
    for asset in assets:
        validate_binary(asset)
        with asset.open("rb") as stream:
            digest = hashlib.file_digest(stream, "sha256").hexdigest()
        lines.append(f"{digest}  {asset.name}")
    manifest = assets_dir / "SHA256SUMS.txt"
    manifest.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return manifest


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="command", required=True)
    for command in ("check-tag", "stage", "checksums"):
        p = sub.add_parser(command)
        p.add_argument("--tag", required=True)
        if command != "checksums":
            p.add_argument("--root", type=Path, default=Path.cwd())
        if command in ("stage", "checksums"):
            p.add_argument("--output", type=Path, default=Path("release-assets"))
        if command == "stage":
            p.add_argument("--kind", required=True, choices=sorted((*FILE_INPUTS, *ARCHIVE_INPUTS)))
    args = parser.parse_args(argv)
    try:
        if args.command == "check-tag":
            print("Release tag matches Android/Desktop: " + check_tag(args.tag, args.root))
        elif args.command == "stage":
            print(stage_asset(args.kind, args.tag, args.root, args.output))
        else:
            print(create_checksum_manifest(args.tag, args.output))
    except (OSError, ValueError) as error:
        print(f"Release artifact validation failed: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
