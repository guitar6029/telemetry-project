export const MessageDefaultConstants = {
    auth: {
        login: {
            success: "Successfully logged in!",
            error: "Issues with logging in."
        },
        logout: {
            error: "Unable to log out."
        }
    },
    organization: {
        load: {
            error: "Could not load organization."
        },
        details: {
            error: "Unable to load organization.",
            errorId: "Organization id is missing."
        },
        creation: {
            success: "Successfully created organization!",
            error: "Organization could not be created."
        },
        update: {
            success: "Successfully updated organization!",
            error: "Organization could not be updated."
        }
    },
    organizations: {
        list: {
            error: "Failed to load organizations."
        }

    }
} as const;
