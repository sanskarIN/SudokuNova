# Exact-Head Verification

A successful workflow run proves only the commit SHA it tested. For PR #28 and future release-hardening work, the branch must stop changing before the final merge evidence is collected.

The required merge pair is:

- `Android CI / verify`;
- `Android Instrumentation / connected-tests`.

Any commit after a green run makes that run historical evidence for the older head. Re-run the required gates on the new exact head before merge. Production signing, physical-device performance, accessibility, repository administration, and store publication remain separate evidence requirements.
