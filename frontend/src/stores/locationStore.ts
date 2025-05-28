import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { locationService } from '@/services';
import {
	Location,
	LocationCreateData,
	LocationUpdateData,
} from '@/models/location';
import { Item } from '@/models/item';

export const useLocationStore = defineStore('location', () => {
	// State - Initialize as empty array to prevent filter errors
	const locations = ref<Location[]>([]);
	const currentLocation = ref<Location | null>(null);
	const loading = ref<boolean>(false);
	const error = ref<string | null>(null);

	// Getters
	const getLocations = computed(() => locations.value || []);
	const getCurrentLocation = computed(() => currentLocation.value);
	const isLoading = computed(() => loading.value);
	const getError = computed(() => error.value);

	// Actions
	const fetchLocations = async (building?: string) => {
		try {
			loading.value = true;
			error.value = null;
			const response = await locationService.getAllLocations(building);
			locations.value = Array.isArray(response) ? response : [];
		} catch (err) {
			error.value =
				err instanceof Error ? err.message : 'Failed to fetch locations';
			locations.value = [];
			console.error('Error fetching locations:', err);
		} finally {
			loading.value = false;
		}
	};

	const fetchLocationById = async (id: number): Promise<Location | null> => {
		try {
			loading.value = true;
			error.value = null;
			const location = await locationService.getLocationById(id);
			currentLocation.value = location;

			// Update the location in the store if it exists
			const index = locations.value.findIndex((loc) => loc.id === id);
			if (index >= 0) {
				locations.value[index] = location;
			} else {
				locations.value.push(location);
			}
			return location;
		} catch (err) {
			error.value =
				err instanceof Error ? err.message : 'Failed to fetch location';
			console.error('Error fetching location by id:', err);
			return null;
		} finally {
			loading.value = false;
		}
	};

	const fetchItemsByLocation = async (locationId: number): Promise<Item[]> => {
		try {
			loading.value = true;
			error.value = null;
			const items = await locationService.getItemsByLocation(locationId);
			return Array.isArray(items) ? items : [];
		} catch (err) {
			error.value =
				err instanceof Error
					? err.message
					: 'Failed to fetch items for location';
			console.error('Error fetching items by location:', err);
			return [];
		} finally {
			loading.value = false;
		}
	};

	const createLocation = async (
		locationData: LocationCreateData
	): Promise<Location | null> => {
		try {
			loading.value = true;
			error.value = null;
			const newLocation = await locationService.createLocation(locationData);
			locations.value.push(newLocation);
			return newLocation;
		} catch (err) {
			error.value =
				err instanceof Error ? err.message : 'Failed to create location';
			console.error('Error creating location:', err);
			return null;
		} finally {
			loading.value = false;
		}
	};

	const updateLocation = async (
		id: number,
		locationData: LocationUpdateData
	): Promise<Location | null> => {
		try {
			loading.value = true;
			error.value = null;
			const updatedLocation = await locationService.updateLocation(
				id,
				locationData
			);
			const index = locations.value.findIndex((location) => location.id === id);
			if (index >= 0) {
				locations.value[index] = updatedLocation;
			}
			if (currentLocation.value?.id === id) {
				currentLocation.value = updatedLocation;
			}
			return updatedLocation;
		} catch (err) {
			error.value =
				err instanceof Error ? err.message : 'Failed to update location';
			console.error('Error updating location:', err);
			return null;
		} finally {
			loading.value = false;
		}
	};

	const deleteLocation = async (id: number): Promise<boolean> => {
		try {
			loading.value = true;
			error.value = null;
			await locationService.deleteLocation(id);
			locations.value = locations.value.filter(
				(location) => location.id !== id
			);
			if (currentLocation.value?.id === id) {
				currentLocation.value = null;
			}
			return true;
		} catch (err) {
			error.value =
				err instanceof Error ? err.message : 'Failed to delete location';
			console.error('Error deleting location:', err);
			return false;
		} finally {
			loading.value = false;
		}
	};

	const clearError = () => {
		error.value = null;
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
		fetchLocationById,
		fetchItemsByLocation,
		createLocation,
		updateLocation,
		deleteLocation,
		clearError,
	};
});
