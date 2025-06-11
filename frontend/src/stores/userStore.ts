import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { userService } from "@/services";
import { User, UserCreateData, UserUpdateData } from "@/models/user";

export const useUserStore = defineStore("user", () => {
    const currentUser = ref<User | null>(null);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    const getCurrentUser = computed(() => currentUser.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Load user from localStorage on store initialization
    const initializeUser = async () => {
        const storedUser = localStorage.getItem('currentUser');
        console.log('Initializing user store, storedUser:', storedUser);
        if (storedUser) {
            try {
                const userData = JSON.parse(storedUser);
                // Verify user still exists by fetching from backend
                if (userData.id) {
                    await fetchUserById(userData.id);
                }
            } catch (error) {
                console.error('Error loading user from localStorage:', error);
                localStorage.removeItem('currentUser');
            }
        }
    };

    const setCurrentUser = (user: User | null) => {
        currentUser.value = user;
        if (user) {
            localStorage.setItem('currentUser', JSON.stringify(user));
            console.log('Current user set:', user);
        } else {
            localStorage.removeItem('currentUser');
        }
    };

    const fetchUserById = async (id: number) => {
        try {
            loading.value = true;
            error.value = null;
            const user = await userService.getUserById(id);
            setCurrentUser(user);
            return user;
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "Failed to fetch user";
            console.error("Error fetching user:", err);
            // Clear invalid user from localStorage
            setCurrentUser(null);
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const createUser = async (userData: UserCreateData) => {
        try {
            loading.value = true;
            error.value = null;
            const newUser = await userService.createUser(userData);
            setCurrentUser(newUser);
            return newUser;
        } catch (err) {
            error.value = err instanceof Error ? err.message : "An error occurred";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const updateUser = async (id: number, userData: UserUpdateData) => {
        try {
            loading.value = true;
            error.value = null;
            const updatedUser = await userService.updateUser(id, userData);
            if (currentUser.value?.id === id) {
                setCurrentUser(updatedUser);
            }
            return updatedUser;
        } catch (err) {
            error.value = err instanceof Error ? err.message : "An error occurred";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const createUserFromContactInfo = async (name: string, email: string) => {
        // Always try to create a new user first
        // This will fail if email already exists, which is what we want
        try {
            return await createUser({ name, email });
        } catch (error) {
            // If creation fails due to email uniqueness, throw the error
            // Don't automatically log in as existing user - that's a security issue
            if (error instanceof Error && error.message.toLowerCase().includes('email')) {
                throw new Error("A user with this email already exists. Please use a different email address.");
            }
            // For other errors, re-throw them
            throw error;
        }
    };

    const deleteUser = async (id: number) => {
        try {
            loading.value = true;
            error.value = null;
            await userService.deleteUser(id);
            // Clear current user if it was the one deleted
            if (currentUser.value?.id === id) {
                setCurrentUser(null);
            }
            return true;
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "An error occurred";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const clearCurrentUser = () => {
        setCurrentUser(null);
    };

    const clearError = () => {
        error.value = null;
    };

    return {
        currentUser,
        loading,
        error,
        getCurrentUser,
        isLoading,
        getError,
        initializeUser,
        fetchUserById,
        createUser,
        updateUser,
        deleteUser,
        createUserFromContactInfo,
        clearCurrentUser,
        clearError,
    };
});
