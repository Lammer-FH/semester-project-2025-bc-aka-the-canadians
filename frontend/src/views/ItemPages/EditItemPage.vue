<template>
  <template-page
    :headline="'Edit Item Details'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleCancel"
    @rightFooterButtonClicked="handleSave"
  >
    <div class="form-container">
      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading item...</p>
      </div>

      <div v-else-if="error && !item.name" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Error Loading</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadItem">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else class="form-content">
        <div class="form-header">
          <ion-icon :icon="createOutline" class="header-icon"></ion-icon>
          <p>Update the details for this item</p>
          <div v-if="item.createdAt" class="last-modified">
            <ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
            Created: {{ formatDate(item.createdAt) }}
          </div>
        </div>

        <div class="input-group">
          <ion-input
            v-model="item.name"
            label="Item Name *"
            label-placement="stacked"
            placeholder="e.g. Black Backpack"
            class="modern-input"
            :class="{
              'input-filled': item.name,
              'input-error': errors.name,
            }"
            @ionBlur="validateField('name')"
          >
          </ion-input>
          <div v-if="errors.name" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.name }}
          </div>
        </div>

        <div class="input-group">
          <ion-textarea
            v-model="item.description"
            label="Description"
            label-placement="stacked"
            placeholder="Detailed description of the item..."
            class="modern-textarea"
            :class="{
              'textarea-filled': item.description,
            }"
            :auto-grow="true"
            :rows="3"
          >
          </ion-textarea>
        </div>

        <div class="divider"></div>

        <div class="context-section">
          <h3 class="context-title">
            <ion-icon
              :icon="informationCircleOutline"
              class="section-icon"
            ></ion-icon>
            Report Context
          </h3>
          <p class="context-description">
            This item belongs to the following report
          </p>

          <div class="context-grid">
            <div class="context-item">
              <ion-icon :icon="documentOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Report ID</label>
                <span>{{ item.reportId }}</span>
              </div>
            </div>

            <div class="context-item">
              <ion-icon :icon="locationOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Location</label>
                <span>{{ item.locationName || "Not specified" }}</span>
              </div>
            </div>

            <div class="context-item">
              <ion-icon :icon="flagOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Report Type</label>
                <span>{{
                  item.reportStatus !== undefined
                    ? item.reportStatus
                      ? "Found Item"
                      : "Lost Item"
                    : "Unknown"
                }}</span>
              </div>
            </div>

            <div class="context-item">
              <ion-icon :icon="personOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Reporter</label>
                <span>{{ item.reporterUserName || "Unknown" }}</span>
              </div>
            </div>

            <div v-if="item.claimedByUserId" class="context-item">
              <ion-icon
                :icon="handRightOutline"
                class="context-icon"
              ></ion-icon>
              <div class="context-content">
                <label>Claimed By</label>
                <span>User ID: {{ item.claimedByUserId }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Delete Button -->
        <div class="input-group">
          <ion-button
            fill="outline"
            color="danger"
            expand="block"
            @click="deleteItem"
          >
            <ion-icon :icon="trashOutline" slot="start"></ion-icon>
            Delete Item
          </ion-button>
        </div>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import {
  IonInput,
  IonTextarea,
  IonButton,
  IonSpinner,
  IonIcon,
} from "@ionic/vue";
import {
  alertCircleOutline,
  checkmarkCircleOutline,
  closeCircleOutline,
  createOutline,
  documentOutline,
  flagOutline,
  handRightOutline,
  informationCircleOutline,
  locationOutline,
  personOutline,
  refreshOutline,
  timeOutline,
  trashOutline,
} from "ionicons/icons";
import TemplatePage from "@/components/TemplatePage.vue";
import { ref, computed, watch, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useItemStore } from "@/stores/itemStore";
import type { Item } from "@/models/item";
import { ItemStatus } from "@/models/item";

const router = useRouter();
const route = useRoute();
const itemStore = useItemStore();

// Initialize with proper default values
const item = ref<Item>({
  id: 0,
  name: "",
  description: "",
  reportId: 0,
  claimedByUserId: undefined,
  createdAt: "",
  status: ItemStatus.UNCLAIMED,
});

const errors = ref({
  name: "",
});

const isLoading = ref(false);
const error = ref<string | null>(null);
const isSaving = ref(false);

const leftFooterButton = computed(() => ({
  name: "Cancel",
  color: "medium",
  icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
  name: isSaving.value
    ? "Saving..."
    : isValid.value
      ? "Save Changes"
      : "Fill Required Fields",
  color: isValid.value ? "primary" : "medium",
  icon: checkmarkCircleOutline,
  disabled: !isValid.value || isSaving.value || isLoading.value,
}));

const isValid = computed(() => {
  return (
    item.value?.name?.trim() !== "" &&
    Object.values(errors.value).every(error => error === "")
  );
});

const formatDate = (dateString: string) => {
  try {
    return new Date(dateString).toLocaleDateString("en-US", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    console.error("Error formatting date:", error);
    return "Invalid Date";
  }
};

const validateField = (fieldName: keyof typeof errors.value) => {
  try {
    const value = String(
      item.value[fieldName as keyof typeof item.value] || ""
    ).trim();

    switch (fieldName) {
      case "name":
        if (!value) {
          errors.value.name = "Name is required";
        } else if (value.length < 2) {
          errors.value.name = "Name must be at least 2 characters long";
        } else {
          errors.value.name = "";
        }
        break;
    }
  } catch (error) {
    console.error("Error validating field:", fieldName, error);
  }
};

const validateAllFields = () => {
  try {
    validateField("name");
  } catch (error) {
    console.error("Error validating all fields:", error);
  }
};

const loadItem = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    const itemId = Number(route.params.id);
    if (!itemId || isNaN(itemId)) {
      throw new Error("Invalid item ID");
    }

    await itemStore.fetchItemById(itemId);
    const loadedItem = itemStore.getCurrentItem;

    if (loadedItem) {
      await nextTick();
      item.value = { ...loadedItem };
    } else {
      throw new Error("Item not found");
    }
  } catch (err) {
    console.error("Error loading item:", err);
    error.value = err instanceof Error ? err.message : "Unknown error";
    setTimeout(() => {
      router.back();
    }, 2000);
  } finally {
    isLoading.value = false;
  }
};

const handleCancel = () => {
  try {
    router.back();
  } catch (error) {
    console.error("Error navigating back:", error);
    router.push("/items/overview");
  }
};

const handleSave = async () => {
  if (!isValid.value) {
    validateAllFields();
    return;
  }

  try {
    isSaving.value = true;

    const updateData: Partial<Item> = {
      name: item.value.name.trim(),
      description: item.value.description?.trim() || "",
    };

    await itemStore.updateItem(item.value.id, updateData);

    // Navigate back to item details
    router.push(`/items/${item.value.id}`);
  } catch (error) {
    console.error("Error saving item:", error);
    // You might want to show an error toast here
  } finally {
    isSaving.value = false;
  }
};

const deleteItem = async () => {
  try {
    await itemStore.deleteItem(item.value.id);
    router.push("/items/overview");
  } catch (error) {
    console.error("Error deleting item:", error);
  }
};

// Watch for name changes to clear errors
watch(
  () => item.value?.name,
  newName => {
    try {
      if (newName && errors.value.name) {
        errors.value.name = "";
      }
    } catch (error) {
      console.error("Error in name watcher:", error);
    }
  }
);

onMounted(() => {
  loadItem();
});
</script>

<style scoped>
.form-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.loading-container p {
  margin-top: 16px;
  color: var(--ion-color-medium);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: var(--ion-color-danger);
  margin-bottom: 16px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.header-icon {
  font-size: 48px;
  color: var(--ion-color-primary);
  margin-bottom: 16px;
}

.form-header h2 {
  color: var(--ion-color-dark);
  margin: 0 0 8px 0;
  font-weight: 600;
}

.form-header p {
  color: var(--ion-color-medium);
  margin: 0 0 16px 0;
  font-size: 0.95em;
}

.last-modified {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: var(--ion-color-light-tint);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85em;
  color: var(--ion-color-medium);
  margin-top: 12px;
}

.time-icon {
  font-size: 14px;
}

.form-content {
  padding: 0;
}

.divider {
  height: 1px;
  background: var(--ion-color-light);
  margin: 24px 0 16px 0;
}

.input-group {
  margin-bottom: 24px;
}

.modern-input,
.modern-textarea {
  --background: var(--ion-color-light-tint);
  --border-radius: 12px;
  --padding-start: 16px;
  --padding-end: 16px;
  --padding-top: 12px;
  --padding-bottom: 12px;
  border: 2px solid var(--ion-color-light-shade);
  border-radius: 12px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.modern-input.input-filled,
.modern-textarea.textarea-filled {
  border-color: var(--ion-color-primary-tint);
  --background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-input:focus-within,
.modern-textarea:focus-within {
  border-color: var(--ion-color-primary);
  box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
  transform: translateY(-2px);
}

.modern-textarea {
  min-height: 80px;
  --padding-start: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--ion-color-light-tint);
  border-radius: 12px;
  border: 1px solid var(--ion-color-light);
}

.info-icon {
  color: var(--ion-color-primary);
  font-size: 20px;
  flex-shrink: 0;
}

.info-text {
  flex: 1;
}

.context-section {
  margin: 24px 0;
  padding: 20px;
  background: var(--ion-color-light-tint);
  border-radius: 12px;
  border: 1px solid var(--ion-color-light);
}

.context-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 8px 0;
  color: var(--ion-color-dark);
  font-size: 16px;
  font-weight: 600;
}

.context-description {
  margin: 0 0 16px 0;
  color: var(--ion-color-medium);
  font-size: 14px;
}

.context-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
}

.context-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--ion-item-background);
  border-radius: 8px;
  border: 1px solid var(--ion-color-light-shade);
}

.context-icon {
  color: var(--ion-color-primary);
  font-size: 18px;
  flex-shrink: 0;
}

.context-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.context-content label {
  font-size: 12px;
  color: var(--ion-color-medium);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.context-content span {
  font-size: 14px;
  color: var(--ion-color-dark);
  font-weight: 500;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--ion-color-danger);
  font-size: 0.85em;
  margin-top: 4px;
  padding-left: 4px;
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

@media (max-width: 768px) {
  .form-container {
    padding: 16px;
  }

  .form-header {
    margin-bottom: 30px;
  }

  .header-icon {
    font-size: 40px;
  }

  .last-modified {
    padding: 6px 12px;
    font-size: 0.8em;
  }
}

@media (max-width: 480px) {
  .form-header {
    margin-bottom: 20px;
  }

  .input-group {
    margin-bottom: 20px;
  }

  .context-section {
    margin: 16px 0;
    padding: 16px;
  }

  .context-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .context-item {
    padding: 10px;
  }
}
</style>
