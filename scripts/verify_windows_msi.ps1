# Verify Windows Installer (MSI) identity without installing/uninstalling anything.
# Run on Windows after :sharedUI:packageMsi; requires Windows Installer COM.
param(
    [Parameter(Mandatory = $true)][string]$Path,
    [Parameter(Mandatory = $true)][string]$ExpectedVersion,
    [string]$ExpectedProductName = "SudokuNova",
    [string]$ExpectedUpgradeCode = "7E35B8B7-C375-5C8F-A3D0-94529BEBF96F"
)

$ErrorActionPreference = "Stop"
$resolved = (Resolve-Path -LiteralPath $Path).Path
if ((Get-Item -LiteralPath $resolved).Length -le 0) {
    throw "MSI file is empty: $resolved"
}

$installer = New-Object -ComObject WindowsInstaller.Installer
$database = $installer.OpenDatabase($resolved, 0)

function Read-MsiProperty([string]$name) {
    # Property names are trusted literals, not user-controlled SQL identifiers.
    $query = 'SELECT `Value` FROM `Property` WHERE `Property`=''{0}''' -f $name
    $view = $database.OpenView($query)
    try {
        $view.Execute()
        $record = $view.Fetch()
        if ($null -eq $record) { throw "Missing MSI property: $name" }
        return [string]$record.StringData(1)
    } finally {
        $view.Close()
    }
}

$product = (Read-MsiProperty "ProductName").Trim()
$version = (Read-MsiProperty "ProductVersion").Trim()
$upgrade = (Read-MsiProperty "UpgradeCode").Trim()
if ($product -ne $ExpectedProductName) {
    throw "Unexpected MSI ProductName '$product' (expected '$ExpectedProductName')"
}
if ($version -ne $ExpectedVersion) {
    throw "Unexpected MSI ProductVersion '$version' (expected '$ExpectedVersion')"
}
if ($upgrade.Trim("{}").ToUpperInvariant() -ne $ExpectedUpgradeCode.ToUpperInvariant()) {
    throw "Unexpected MSI UpgradeCode '$upgrade'"
}

Write-Host "MSI verified: ProductName=$product ProductVersion=$version UpgradeCode=$upgrade"
