import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { locationService } from '@/services';
import { Location, LocationCreateData, LocationUpdateData } from '@/models/location';
import { Item } from '@/models/item';

export const useLocationStore = defineStore('location', () => {
    // State
    const locations = ref<Location[]>([]);
    const currentLocation = ref<Location | null>(null);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    // Getters
    const getLocations = computed(() => locations.value);
    const getCurrentLocation = computed(() => currentLocation.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Actions
    const fetchLocations = async () => {
        try {
            loading.value = true;
            locations.value = await locationService.getAllLocations();
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
        } finally {
            loading.value = false;
        }
    };

    const fetchItemsByLocation = async (locationId: number) => {
        try {
            loading.value = true;
            const items = await locationService.getItemsByLocation(locationId);
            return items;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const createLocation = async (locationData: LocationCreateData) => {
        try {
            loading.value = true;
            const newLocation = await locationService.createLocation(locationData);
            locations.value.push(newLocation);
            return newLocation;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const updateLocation = async (id: number, locationData: LocationUpdateData) => {
        try {
            loading.value = true;
            const updatedLocation = await locationService.updateLocation(id, locationData);
            const index = locations.value.findIndex(location => location.id === id);
            if (index !== -1) {
                locations.value[index] = updatedLocation;
            }
            return updatedLocation;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const deleteLocation = async (id: number) => {
        try {
            loading.value = true;
            await locationService.deleteLocation(id);
            locations.value = locations.value.filter(location => location.id !== id);
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
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