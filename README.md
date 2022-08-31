# Max-Commit-Count

A GitHub Action that limits the number of commits allowed in a PR to a configurable number.

Using Max-Commit-Count is pretty simple, just make a file in your repo like so:

`.github/workflows/max-commits.yml`:

```yml
name: Max Commits

on:
  pull_request:
    branches:
      - main

jobs:
  testing-self:
    runs-on: ubuntu-latest
    steps:
      - uses: nikwotton/Max-Commit-Count@v1.0.0
        with:
          maxCommits: 3
          token: ${{ secrets.GITHUB_TOKEN }}
```

The only fields required (and allowed) by this action are:

- `maxCommits`: The maximum number of commits you want to exist in a PR, inclusive.
- `token`: The GitHub token to be used for querying the number of commits in the PR.  
