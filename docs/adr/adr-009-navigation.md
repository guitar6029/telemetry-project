Navigation & Browser History
Browser History vs Application State

The browser's history stack only stores where the user has been, not the application's state.

When navigating back to a page, the application should reconstruct the current UI from the server (or other authoritative state) rather than attempting to restore a stale UI.

Choosing Between navigate() and replaceUrl
Use normal navigation (navigate)

Use standard navigation when the previous page remains a meaningful destination.

Examples:

Dashboard
Device List
Device Details
Organization List
Alerts

Users should naturally be able to return to these pages using the browser's Back button.

Use replaceUrl

Use replaceUrl when the current page represents a completed workflow or temporary transition.

Examples:

Logout
Login (when appropriate)
OAuth callback
Password reset completion
Create Device → Device Details
Create Organization → Organization Details

These pages represent a completed flow rather than a destination the user should revisit.

Guiding Principle

When deciding between navigate() and replaceUrl, ask:

If the user presses the browser Back button right now, where would they reasonably expect to end up?

The answer should drive the navigation behavior rather than implementation details.

Security vs User Experience

Authentication guards provide security.

Browser history provides user experience.

Removing a page from history using replaceUrl should improve navigation flow—not be relied upon for security.

Protected routes must always remain protected by authorization guards regardless of browser history.
