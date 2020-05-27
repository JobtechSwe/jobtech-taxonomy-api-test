# jobtech-taxonomy-api-test

Tests for the API server in https://gitlab.com/team-batfish/jobtech-taxonomy-api

## Installation

Clone this repo.

## Run the tests

The tests are configured via two environment variables. Set those before running the tests.

```shell
export JOBTECH_TAXONOMY_API_KEY=111
export JOBTECH_TAXONOMY_API_URL=http://localhost:3000
lein test
```

## License

Copyright © 2019 Jobtech

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
