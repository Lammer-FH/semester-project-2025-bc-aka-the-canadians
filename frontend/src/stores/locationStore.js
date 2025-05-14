import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { locationService } from '@/services';

export const useLocationStore = defineStore('location', () => {
    // State
    const locations = ref([]);
    const currentLocation = ref(null);
    const loading = ref(false);
    const error = ref(null);

    // Getters
    const getLocations = computed(() => locations.value);
    const getCurrentLocation = computed(() => currentLocation.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Actions
    const fetchLocations = async () => {
        // TODO: Implement API call
    };

    const fetchItemsByLocation = async (locationId) => {
        // TODO: Implement API call
    };

    const createLocation = async (locationData) => {
        // TODO: Implement API call
    };

    const updateLocation = async (id, locationData) => {
        // TODO: Implement API call
    };

    const deleteLocation = async (id) => {
        // TODO: Implement API call
    };

    return {
        // State
        locations,
        currentLocation,
        loading,
        error,
        // Getters
        getLocations,
        getCurrentLocation,
        isLoading,
        getError,
        // Actions
        fetchLocations,
        fetchItemsByLocation,
        createLocation,
        updateLocation,
        deleteLocation
    };
}); 