# v1.0 Validation-Hardening Branch Freeze

PR #28 is intended to merge only after the exact final branch head passes both required repository workflows. Earlier successful or queued workflow runs apply only to the commit they tested.

Required merge gates:

- `Android CI / verify`;
- `Android Instrumentation / connected-tests`.

If the branch changes after a successful run, the relevant exact-head verification must be repeated. This source-controlled rule does not replace the remaining physical-device, production-signing, repository-admin, store, or stable-publication evidence tracked in issue #5.
