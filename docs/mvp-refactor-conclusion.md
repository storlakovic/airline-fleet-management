## MVP Conclusion and Next Steps

With this refactoring phase completed, the current backend represents the finished state of the initial MVP.

The main domain features are implemented, the previously identified inconsistencies have been reviewed, and the codebase now provides a more consistent foundation for further development. The goal of this stage was not to solve every future operational scenario, but to establish a functional baseline before expanding the project.

Several limitations are already known and will be addressed as the project evolves:

- Time zone handling is not yet fully aligned with how flight operations should work across different airports and regions.
- Flight times currently require too much manual input. Future simulation functionality should allow recurring and routinely scheduled flights to be generated automatically.
- Concurrent operations may currently lead to consistency problems when multiple actions modify related data at the same time. Concurrency handling will be addressed separately.
- Airport status is not yet considered consistently when creating, scheduling, or operating flights.
- Some `FlightStatus` states are not yet fully integrated into every relevant business rule and simulation transition.
- The current time and scheduling model is intentionally still simple and will be revisited as part of the Simulation feature.

These are known limitations rather than unnoticed implementation gaps. They are intentionally left for later stages where the surrounding architecture can be designed around them instead of adding isolated fixes to the MVP.

The project now moves beyond the initial backend MVP.

The next major development areas are the user interface and a dedicated Simulation section. The Simulation feature will gradually take over responsibilities such as automatic time progression, recurring flight schedules, operational state changes, and other time-driven behavior.

From this point forward, new functionality should build on the cleaned-up domain model rather than continuing to extend the original MVP structure.