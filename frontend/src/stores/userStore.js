import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useUserStore = defineStore('user', () => {
    // State
    const currentUser = ref(null);
    const loading = ref(false);
    const error = ref(null);

    // Getters
    const getCurrentUser = computed(() => currentUser.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Actions
    const createUser = async (userData) => {
        // TODO: Implement API call
    };

    const updateUser = async (id, userData) => {
        // TODO: Implement API call
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