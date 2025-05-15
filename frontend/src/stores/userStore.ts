import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { userService } from '@/services';
import { User, UserCreateData, UserUpdateData } from '@/models/user';

export const useUserStore = defineStore('user', () => {
    // State
    const currentUser = ref<User | null>(null);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    // Getters
    const getCurrentUser = computed(() => currentUser.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Actions
    const createUser = async (userData: UserCreateData) => {
        try {
            loading.value = true;
            const newUser = await userService.createUser(userData);
            currentUser.value = newUser;
            return newUser;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const updateUser = async (id: number, userData: UserUpdateData) => {
        try {
            loading.value = true;
            const updatedUser = await userService.updateUser(id, userData);
            if (currentUser.value?.id === id) {
                currentUser.value = updatedUser;
            }
            return updatedUser;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        // State
        currentUser,
        loading,
        error,
        // Getters
        getCurrentUser,
        isLoading,
        getError,
        // Actions
        createUser,
        updateUser
    };
}); 