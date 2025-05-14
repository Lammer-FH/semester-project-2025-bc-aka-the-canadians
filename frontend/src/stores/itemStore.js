import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { itemService } from '@/services';

export const useItemStore = defineStore('item', () => {
    // State
    const items = ref([]);
    const currentItem = ref(null);
    const loading = ref(false);
    const error = ref(null);

    // Getters
    const getItems = computed(() => items.value);
    const getCurrentItem = computed(() => currentItem.value);
    const getError = computed(() => error.value);

    // Actions
    const fetchItems = async (filters = {}) => {
        try {
            items.value = await itemService.getAllItems(filters);
        } catch (err) {
            error.value = err.message;
        }
    };

    const fetchItemById = async (id) => {
        try {
            currentItem.value = await itemService.getItemById(id);
        } catch (err) {
            error.value = err.message;
        }
    };

    const updateItem = async (id, itemData) => {
        try {
            const updatedItem = await itemService.updateItem(id, itemData);
            const index = items.value.findIndex(item => item.id === id);
            if (index !== -1) {
                items.value[index] = updatedItem;
            }
            return updatedItem;
        } catch (err) {
            error.value = err.message;
        }
    };

    const deleteItem = async (id) => {
        try {
            await itemService.deleteItem(id);
            items.value = items.value.filter(item => item.id !== id);
        } catch (err) {
            error.value = err.message;
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