<template>
  <template-page
    :headline="'Edit Item'"
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
          <h2>Edit Item</h2>
          <p>Update the details for this item</p>
          <div v-if="item.updatedAt" class="last-modified">
            <ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
            Last modified: {{ formatDate(item.updatedAt) }}
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
            <ion-icon
              :icon="textOutline"
              slot="start"
              class="input-icon"
            ></ion-icon>
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
            rows="3"
          >
            <ion-icon
              :icon="documentTextOutline"
              slot="start"
              class="input-icon"
            ></ion-icon>
          </ion-textarea>
        </div>

        <div class="input-group">
          <ion-input
            v-model="item.location"
            label="Found/Lost Location *"
            label-placement="stacked"
            placeholder="e.g. Library, Lecture Hall A1, Cafeteria"
            class="modern-input"
            :class="{
              'input-filled': item.location,
              'input-error': errors.location,
            }"
            @ionBlur="validateField('location')"
          >
            <ion-icon
              :icon="locationOutline"
              slot="start"
              class="input-icon"
            ></ion-icon>
          </ion-input>
          <div v-if="errors.location" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.location }}
          </div>
        </div>

        <div class="input-group">
          <ion-select
            v-model="item.status"
            label="Status *"
            label-placement="stacked"
            placeholder="Choose the status"
            interface="popover"
            class="modern-select"
            :class="{
              'select-filled': item.status,
              'select-error': errors.status,
            }"
            @ionChange="validateField('status')"
          >
            <ion-icon
              :icon="flagOutline"
              slot="start"
              class="input-icon"
            ></ion-icon>
            <ion-select-option value="LOST">
              <div class="select-option">
                <ion-icon :icon="searchOutline" class="option-icon"></ion-icon>
                <span>Lost</span>
              </div>
            </ion-select-option>
            <ion-select-option value="FOUND">
              <div class="select-option">
                <ion-icon :icon="eyeOutline" class="option-icon"></ion-icon>
                <span>Found</span>
              </div>
            </ion-select-option>
            <ion-select-option value="CLAIMED">
              <div class="select-option">
                <ion-icon
                  :icon="checkmarkCircleOutline"
                  class="option-icon"
                ></ion-icon>
                <span>Claimed</span>
              </div>
            </ion-select-option>
          </ion-select>
          <div v-if="errors.status" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.status }}
          </div>
        </div>

        <div class="input-group">
          <div class="image-upload-section">
            <h4 class="upload-title">
              <ion-icon :icon="cameraOutline" class="title-icon"></ion-icon>
              Image (optional)
            </h4>
            <p class="upload-description">
              Add an image to help identify the item better.
            </p>

            <div
              v-if="imagePreview || item.imageUrl"
              class="image-preview-container"
            >
              <img
                :src="imagePreview || item.imageUrl"
                :alt="item.name"
                class="image-preview"
              />
              <ion-button
                fill="clear"
                color="danger"
                class="remove-image-btn"
                @click="removeImage"
              >
                <ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
              </ion-button>
            </div>

            <div class="upload-buttons">
              <ion-button fill="outline" class="upload-btn" @click="takePhoto">
                <ion-icon :icon="cameraOutline" slot="start"></ion-icon>
                Take Photo
              </ion-button>
              <ion-button
                fill="outline"
                class="upload-btn"
                @click="triggerFileInput"
              >
                <ion-icon :icon="cloudUploadOutline" slot="start"></ion-icon>
                Upload File
              </ion-button>
            </div>

            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleFileSelect"
            />
          </div>
        </div>

        <div class="action-buttons">
          <ion-button
            fill="outline"
            color="danger"
            expand="block"
            class="delete-button"
            @click="handleDelete"
          >
            <ion-icon :icon="trashOutline" slot="start"></ion-icon>
            Delete Item
          </ion-button>
        </div>

        <div class="form-footer-info">
          <div class="info-item">
            <ion-icon
              :icon="informationCircleOutline"
              color="primary"
              class="info-icon"
            ></ion-icon>
            <div class="info-text">
              <p>
                All changes will be automatically saved when you click "Save
                Changes".
              </p>
              <p>Fields marked with * are required.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ion-alert
      :is-open="showDeleteAlert"
      header="Delete Item"
      message="Are you sure you want to delete this item? This action cannot be undone."
      :buttons="alertButtons"
      @didDismiss="showDeleteAlert = false"
    ></ion-alert>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import {
  IonInput,
  IonTextarea,
  IonIcon,
  IonButton,
  IonSpinner,
  IonAlert,
  IonSelect,
  IonSelectOption,
} from "@ionic/vue";
import {
  createOutline,
  textOutline,
  documentTextOutline,
  locationOutline,
  flagOutline,
  alertCircleOutline,
  informationCircleOutline,
  checkmarkCircleOutline,
  closeCircleOutline,
  trashOutline,
  timeOutline,
  cameraOutline,
  cloudUploadOutline,
  refreshOutline,
  eyeOutline,
  searchOutline,
} from "ionicons/icons";
import { ref, computed, watch, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useItemStore } from "@/stores/itemStore";
import type { Item } from "@/models/item";

const router = useRouter();
const route = useRoute();
const itemStore = useItemStore();

// Initialize with proper default values
const item = ref<Item>({
  id: 0,
  name: "",
  description: "",
  location: "",
  status: "",
  createdAt: "",
  updatedAt: "",
  imageUrl: "",
});

const errors = ref({
  name: "",
  location: "",
  status: "",
});

const isLoading = ref(false);
const error = ref<string | null>(null);
const isSaving = ref(false);
const showDeleteAlert = ref(false);
const imagePreview = ref("");
const fileInput = ref<HTMLInputElement>();

// Use local loading state instead of store loading state
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
    item.value?.location?.trim() !== "" &&
    item.value?.status?.trim() !== "" &&
    Object.values(errors.value).every(error => error === "")
  );
});

const alertButtons = [
  {
    text: "Cancel",
    role: "cancel",
    cssClass: "alert-button-cancel",
  },
  {
    text: "Delete",
    role: "destructive",
    cssClass: "alert-button-confirm",
    handler: () => confirmDelete(),
  },
];

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
      case "location":
        if (!value) {
          errors.value.location = "Location is required";
        } else {
          errors.value.location = "";
        }
        break;
      case "status":
        if (!value) {
          errors.value.status = "Status is required";
        } else {
          errors.value.status = "";
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
    validateField("location");
    validateField("status");
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
      // Use nextTick to ensure reactivity is properly handled
      await nextTick();
      item.value = { ...loadedItem };
    } else {
      throw new Error("Item not found");
    }
  } catch (err) {
    console.error("Error loading item:", err);
    error.value = err instanceof Error ? err.message : "Unknown error";
    // Don't navigate away immediately, let user see the error
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
      location: item.value.location.trim(),
      status: item.value.status,
    };

    if (imagePreview.value) {
      updateData.imageData = imagePreview.value;
    }

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

const handleDelete = () => {
  showDeleteAlert.value = true;
};

const confirmDelete = async () => {
  try {
    await itemStore.deleteItem(item.value.id);
    router.push("/items/overview");
  } catch (error) {
    console.error("Error deleting item:", error);
    // You might want to show an error toast here
  }
};

const triggerFileInput = () => {
  try {
    fileInput.value?.click();
  } catch (error) {
    console.error("Error triggering file input:", error);
  }
};

const handleFileSelect = (event: Event) => {
  try {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];

    if (file) {
      if (!file.type.startsWith("image/")) {
        console.error("Please select an image file");
        return;
      }

      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        console.error("File size must be less than 5MB");
        return;
      }

      const reader = new FileReader();
      reader.onload = e => {
        if (e.target?.result) {
          imagePreview.value = e.target.result as string;
        }
      };
      reader.onerror = error => {
        console.error("Error reading file:", error);
      };
      reader.readAsDataURL(file);
    }
  } catch (error) {
    console.error("Error handling file select:", error);
  }
};

const takePhoto = async () => {
  try {
    triggerFileInput();
  } catch (error) {
    console.error("Error taking photo:", error);
  }
};

const removeImage = () => {
  try {
    imagePreview.value = "";
    if (item.value) {
      item.value.imageUrl = "";
    }
    if (fileInput.value) {
      fileInput.value.value = "";
    }
  } catch (error) {
    console.error("Error removing image:", error);
  }
};

// Wrap watchers in try-catch and add null checks
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

watch(
  () => item.value?.location,
  newLocation => {
    try {
      if (newLocation && errors.value.location) {
        errors.value.location = "";
      }
    } catch (error) {
      console.error("Error in location watcher:", error);
    }
  }
);

watch(
  () => item.value?.status,
  newStatus => {
    try {
      if (newStatus && errors.value.status) {
        errors.value.status = "";
      }
    } catch (error) {
      console.error("Error in status watcher:", error);
    }
  }
);

onMounted(async () => {
  try {
    await loadItem();
  } catch (error) {
    console.error("Error in onMounted:", error);
  }
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
  padding: 60px 20px;
  text-align: center;
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
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  color: var(--ion-color-medium);
  margin-bottom: 20px;
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
  animation: slideInUp 0.6s ease-out;
}

.input-group {
  margin-bottom: 24px;
}

.modern-input,
.modern-textarea,
.modern-select {
  --background: var(--ion-color-light-tint);
  --border-radius: 12px;
  --padding-start: 48px;
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
.modern-textarea.textarea-filled,
.modern-select.select-filled {
  border-color: var(--ion-color-primary-tint);
  --background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-input:focus-within,
.modern-textarea:focus-within,
.modern-select:focus-within {
  border-color: var(--ion-color-primary);
  box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
  transform: translateY(-2px);
}

.modern-textarea {
  min-height: 80px;
  --padding-start: 48px;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--ion-color-primary);
  font-size: 18px;
  z-index: 10;
}

.select-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.option-icon {
  font-size: 18px;
  color: var(--ion-color-primary);
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: transparent;
  padding: 0;
}

.info-icon {
  font-size: 20px;
  margin-top: 2px;
  flex-shrink: 0;
}

.info-text {
  flex: 1;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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

  .action-buttons {
    margin: 30px 0;
    padding: 16px;
  }
}
</style>
