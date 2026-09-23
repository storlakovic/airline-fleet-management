## Development Workflow

As the project grows, changes are developed on dedicated branches instead of being committed directly to `main`.

This keeps changes isolated, makes reviews easier, and reduces the risk of unrelated changes being mixed together.

### Branching

- Branches are created from `main`
- Branch names should describe the type and scope of the change
- Related tasks may be grouped in one branch when they belong to the same scoped change

Examples:

- `feature/flight-management`
- `refactor/aircraft-refactor`
- `fix/aircraft-details-path-variable`

### Pull Requests

- Changes are merged into `main` through pull requests
- CI should pass before merging