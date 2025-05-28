import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { itemService } from '@/services';
import { Item, ItemFilters } from '@/models/item';

export const useItemStore = defineStore('item', () => {
	// State - Initialize as empty array to prevent filter errors
	const items = ref<Item[]>([]);
	const currentItem = ref<Item | null>(null);
	const loading = ref<boolean>(false);
	const error = ref<string | null>(null);

	// Getters
	const getItems = computed(() => items.value || []); // Ensure it's always an array
	const getCurrentItem = computed(() => currentItem.value);
	const getError = computed(() => error.value);
	const isLoading = computed(() => loading.value);

	// Actions
	const fetchItems = async (filters: ItemFilters = {}) => {
		try {
			loading.value = true;
			error.value = null; // Clear previous errors
			const response = await itemService.getAllItems(filters);
			items.value = Array.isArray(response) ? response : []; // Ensure it's an array
		} catch (err) {
			error.value = err instanceof Error ? err.message : 'An error occurred';
			items.value = []; // Set to empty array on error
			console.error('Error fetching items:', err);
		} finally {
			loading.value = false;
		}
	};

	const fetchItemById = async (id: number): Promise<Item | null> => {
		try {
			loading.value = true;
			error.value = null;
			const item = await itemService.getItemById(id);
			currentItem.value = item;

			// Update the item in the items array if it exists
			const index = items.value.findIndex((i) => i.id === id);
			if (index >= 0) {
				items.value[index] = item;
			} else {
				items.value.push(item);
			}
			return item;
		} catch (err) {
			error.value = err instanceof Error ? err.message : 'Failed to fetch item';
			console.error('Error fetching item by id:', err);
			return null;
		} finally {
			loading.value = false;
		}
	};

	const updateItem = async (
		id: number,
		itemData: Partial<Item>
	): Promise<Item | null> => {
		try {
			loading.value = true;
			error.value = null;
			const updatedItem = await itemService.updateItem(id, itemData);

			// Update current item if it's the same
			if (currentItem.value?.id === id) {
				currentItem.value = updatedItem;
			}

			// Update the item in the items array
			const index = items.value.findIndex((item) => item.id === id);
			if (index !== -1) {
				items.value[index] = updatedItem;
			}
			return updatedItem;
		} catch (err) {
			error.value =
				err instanceof Error ? err.message : 'Failed to update item';
			console.error('Error updating item:', err);
			return null;
		} finally {
			loading.value = false;
		}
	};

	const deleteItem = async (id: number): Promise<boolean> => {
		try {
			loading.value = true;
			error.value = null;
			await itemService.deleteItem(id);
			items.value = items.value.filter((item) => item.id !== id);

			// Clear current item if it's the deleted one
			if (currentItem.value?.id === id) {
				currentItem.value = null;
			}
			return true;
		} catch (err) {
			error.value =
				err instanceof Error ? err.message : 'Failed to delete item';
			console.error('Error deleting item:', err);
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
		items,
		currentItem,
		loading,
		error,
		// Getters
		getItems,
		getCurrentItem,
		isLoading,
		getError,
		// Actions
		fetchItems,
		fetchItemById,
		updateItem,
		deleteItem,
		clearError,
	};
});
