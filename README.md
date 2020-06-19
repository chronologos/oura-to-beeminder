
# oura-to-beeminder

A Clojure library designed to sync sleep score data from your Oura ring to Beeminder. By default, it will attempt to idempotently update sleep data for the last 4 days in Beeminder. It will not update days for which there is already sleep data, even if the data differs from what is in Oura.

You will need an Oura developer auth token as well as a Beeminder developer auth token.

## Usage

Create `profiles.clj` if you are developing. It will look like this:

```
{:dev  {:env {:oura-token "SOME_TOKEN1"
              :beeminder-token "SOME_TOKEN2"
              :beeminder-user "USERNAME1"
              :beeminder-goal-id "GOAL_ID1"}}
 :test {:env {:oura-token "SOME_TOKEN1"
              :beeminder-token "SOME_TOKEN2"
              :beeminder-user "USERNAME1"
              :beeminder-goal-id "GOAL_ID1"}}}
```

Run with `lein run`.

You can also compile with `lein uberjar` and run the binary with `BEEMINDER_TOKEN=x OURA_TOKEN=y java -jar beeminder_to_oura-standalone.jar`

## License

Copyright © 2020 chronologos

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
