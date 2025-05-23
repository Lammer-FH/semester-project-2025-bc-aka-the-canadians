import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { itemService } from '@/services';
import { Item, ItemFilters } from '@/models/item';

export const useItemStore = defineStore('item', () => {
    // State
    const items = ref<Item[]>([]);
    const currentItem = ref<Item | null>(null);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    // Getters
    const getItems = computed(() => items.value);
    const getCurrentItem = computed(() => currentItem.value);
    const getError = computed(() => error.value);
    const isLoading = computed(() => loading.value);

    // Actions
    const fetchItems = async (filters: ItemFilters = {}) => {
        try {
            loading.value = true;
            items.value = await itemService.getAllItems(filters);
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
        } finally {
            loading.value = false;
        }
    };

    const fetchItemById = async (id: number) => {
        try {
            loading.value = true;
            currentItem.value = await itemService.getItemById(id);
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
        } finally {
            loading.value = false;
        }
    };

    const updateItem = async (id: number, itemData: Partial<Item>) => {
        try {
            loading.value = true;
            const updatedItem = await itemService.updateItem(id, itemData);
            const index = items.value.findIndex(item => item.id === id);
            if (index !== -1) {
                items.value[index] = updatedItem;
            }
            return updatedItem;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const deleteItem = async (id: number) => {
        try {
            loading.value = true;
            await itemService.deleteItem(id);
            items.value = items.value.filter(item => item.id !== id);
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
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
        deleteItem
    };
}); 