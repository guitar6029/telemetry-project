Architectural Decision

We investigated using an Angular component to display a loading screen during application bootstrap. Because provideAppInitializer() blocks Angular from creating the component tree until initialization completes, Angular components cannot participate in pre-bootstrap rendering. Startup loading will instead be implemented as a static splash in index.html. Angular loading components remain appropriate for feature- and route-level loading after bootstrap.
