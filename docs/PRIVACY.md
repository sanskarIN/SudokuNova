# SudokuNova Privacy Policy

Last updated: 2026-08-17

This policy describes the behavior of the current open-source SudokuNova base application represented by this repository. It must be reviewed if application behavior changes before distribution.

## Data Collected by SudokuNova

The current base application does not include remote analytics, advertising, user accounts, or a SudokuNova-operated network backend. It does not request sensitive permissions such as contacts, location, microphone, camera, SMS, or call logs.

## Data Stored Locally

SudokuNova stores on the Android device:

- Gameplay preferences/settings
- Active puzzle state needed for resume
- Notes and current board state in the active save
- Elapsed time, mistakes, and hints used for the active game
- Aggregate gameplay statistics and streak values

The current implementation uses Android Preferences DataStore.

## Why Data Is Stored

Local data is used to:

- Remember user preferences
- Restore an active Sudoku after app/process restart
- Show local statistics and achievements
- Maintain local streak calculations

## Data Transmission

The current base application does not transmit gameplay data to SudokuNova servers because no such backend is configured.

When the user explicitly taps external links such as GitHub, Buy Me a Coffee, or email support, Android opens the relevant external app/service. Those external services have their own privacy practices.

## Data Deletion

Current Settings includes local statistics reset. Active game data is removed when a completed game is recorded and can be replaced by starting another game. Expanded controls for clearing all saved games/settings are planned before stable release.

Users may also use Android system app-data clearing/uninstall behavior to remove application-local data.

## Android Backup

The repository currently allows standard Android backup/device-transfer domains through XML backup rules. Actual backup behavior can depend on Android/device configuration and distribution settings. This policy should be re-reviewed before production release to ensure backup behavior matches product expectations.

## Children and Accounts

SudokuNova does not currently require an account or ask users to submit profile information through the app.

## Changes

If analytics, crash reporting, cloud sync, accounts, ads, or other data-transmitting services are added in a future optional build, this policy must be updated before the feature is represented as released. Consent/controls must match applicable platform/legal requirements.

## Contact

Support: `supportramsandesh@gmail.com`  
Business: `sanskarin@outlook.in`  
Business: `sanskarin.business@gmail.com`

Repository: https://github.com/sanskarIN/SudokuNova

**Made by the Sanskar**
