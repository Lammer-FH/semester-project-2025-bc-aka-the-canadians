<template>
  <template-page
    :headline="location?.name || 'Location Details'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleBack"
    @rightFooterButtonClicked="handleEdit"
  >
    <div class="details-container">
      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading location...</p>
      </div>

      <div v-else-if="error && !location" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Error Loading</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadLocation">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else-if="location" class="content-wrapper">
        <div class="location-header">
          <div class="header-content">
            <ion-icon :icon="locationOutline" class="location-icon"></ion-icon>
            <div class="header-text">
              <h1>{{ location.name }}</h1>
            </div>
          </div>
          <div class="quick-actions">
            <ion-button fill="clear" size="small" @click="handleEdit">
              <ion-icon :icon="createOutline" slot="icon-only"></ion-icon>
            </ion-button>
          </div>
        </div>

        <div v-if="location.description" class="description-section">
          <h3>Description</h3>
          <p>{{ location.description }}</p>
        </div>

        <!-- Items List -->
        <ion-card class="items-card">
          <ion-card-header>
            <ion-card-title>
              <ion-icon :icon="cubeOutline" class="section-icon"></ion-icon>
              Items at this Location
              <ion-chip color="primary" style="margin-left: 12px">
                <ion-icon :icon="bagOutline" class="chip-icon"></ion-icon>
                {{ itemsAtLocation.length }}
              </ion-chip>
            </ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <div v-if="itemsLoading" class="loading-container">
              <ion-spinner name="crescent" color="primary"></ion-spinner>
              <p>Loading items...</p>
            </div>
            <div
              v-else-if="itemsAtLocation && itemsAtLocation.length > 0"
              class="items-list"
            >
              <div
                v-for="item in itemsAtLocation"
                :key="item.id"
                class="item-card"
                @click="navigateToItem(item.id)"
              >
                <div class="item-header">
                  <h3>{{ item.name }}</h3>
                </div>
                <p v-if="item.description" class="item-description">
                  {{ truncateDescription(item.description, 100) }}
                </p>
                <div class="item-meta">
                  <span class="meta-item">
                    <ion-icon :icon="timeOutline" class="meta-icon"></ion-icon>
                    {{ formatDate(item.createdAt) }}
                  </span>
                  <span v-if="item.claimedByUser" class="meta-item">
                    <ion-icon
                      :icon="personOutline"
                      class="meta-icon"
                    ></ion-icon>
                    Claimed by {{ item.claimedByUser.name }}
                  </span>
                </div>
              </div>
            </div>
            <div v-else class="empty-items">
              <ion-icon :icon="cubeOutline" class="empty-icon"></ion-icon>
              <h3>No Items</h3>
              <p>No items have been found at this location yet.</p>
            </div>
          </ion-card-content>
        </ion-card>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import {
  IonIcon,
  IonButton,
  IonChip,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonSpinner,
} from "@ionic/vue";
import {
  locationOutline,
  createOutline,
  refreshOutline,
  bagOutline,
  alertCircleOutline,
  arrowBack,
  timeOutline,
  personOutline,
  cubeOutline,
} from "ionicons/icons";
import { ref, computed, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useLocationStore } from "@/stores/locationStore";
import type { Location } from "@/models/location";
import type { Item } from "@/models/item";

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const truncateDescription = (
  description: string,
  maxLength: number = 100
): string => {
  if (description.length <= maxLength) return description;
  return description.substring(0, maxLength) + "...";
};

const router = useRouter();
const route = useRoute();
const locationStore = useLocationStore();

const location = ref<Location | null>(null);
const itemsAtLocation = ref<Item[]>([]);
const itemsLoading = ref(false);

const localIsLoading = ref(false);
const localError = ref<string | null>(null);

const isLoading = computed(
  () => localIsLoading.value || (locationStore.isLoading && !location.value)
);
const error = computed(() => localError.value || locationStore.getError);

const leftFooterButton = computed(() => ({
  name: "Back",
  color: "medium",
  icon: arrowBack,
}));

const rightFooterButton = computed(() => ({
  name: "Edit",
  color: "primary",
  icon: createOutline,
}));

const loadLocation = async () => {
  try {
    localIsLoading.value = true;
    localError.value = null;

    const locationId = Number(route.params.id);
    if (!locationId || isNaN(locationId)) {
      throw new Error("Invalid location ID");
    }

    await locationStore.fetchLocationById(locationId);
    const loadedLocation = locationStore.getCurrentLocation;

    if (loadedLocation) {
      await nextTick();
      location.value = { ...loadedLocation };
    } else {
      throw new Error("Location not found");
    }
  } catch (err) {
    console.error("Error loading location:", err);
    localError.value = err instanceof Error ? err.message : "Unknown error";
    setTimeout(() => {
      router.back();
    }, 2000);
  } finally {
    localIsLoading.value = false;
  }
};

const loadItemsAtLocation = async (locationId: number) => {
  try {
    itemsLoading.value = true;
    // Use the correct store method name
    const allItems = await locationStore.fetchItemsByLocation(locationId);
    itemsAtLocation.value = allItems || [];
  } catch (err) {
    console.error("Error loading items at location:", err);
    itemsAtLocation.value = [];
  } finally {
    itemsLoading.value = false;
  }
};

const handleBack = () => {
  router.back();
};

const handleEdit = () => {
  if (location.value?.id) {
    router.push(`/locations/${location.value.id}/edit`);
  }
};

const navigateToItem = (itemId: number) => {
  router.push(`/items/${itemId}`);
};

onMounted(async () => {
  await loadLocation();
  if (location.value?.id) {
    await loadItemsAtLocation(location.value.id);
  }
});
</script>

<style scoped>
.details-container {
  min-height: 100vh;
  background: var(--ion-color-light-tint);
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

.empty-state h2 {
  color: var(--ion-color-dark);
  margin-bottom: 10px;
  font-weight: 600;
}

.empty-state p {
  color: var(--ion-color-medium);
  margin-bottom: 30px;
  line-height: 1.5;
}

.content-wrapper {
  padding: 20px;
  animation: fadeInUp 0.6s ease-out;
}

.location-header {
  background: linear-gradient(
    135deg,
    var(--ion-color-primary),
    var(--ion-color-primary-shade)
  );
  color: white;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(var(--ion-color-primary-rgb), 0.3);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
}

.location-header::before {
  content: "";
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    transparent
  );
  transform: rotate(45deg);
  transition: all 0.6s ease;
}

.header-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  flex: 1;
}

.location-icon {
  font-size: 40px;
  opacity: 0.9;
  margin-top: 4px;
}

.header-text h1 {
  color: white;
  margin: 0 0 12px 0;
  font-size: 1.5em;
  font-weight: 600;
}

.location-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9em;
}

.info-icon {
  font-size: 16px;
  opacity: 0.8;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.description-section,
.metadata-section,
.items-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.description-section h3,
.metadata-section h3,
.items-section h3 {
  color: var(--ion-color-dark);
  margin: 0 0 16px 0;
  font-size: 1.1em;
  font-weight: 600;
}

.description-section p {
  color: var(--ion-color-medium-shade);
  line-height: 1.6;
  margin: 0;
}

.metadata-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--ion-color-light-tint);
  border-radius: 8px;
}

.metadata-icon {
  font-size: 20px;
  color: var(--ion-color-primary);
}

.metadata-content {
  display: flex;
  flex-direction: column;
}

.metadata-label {
  font-size: 0.8em;
  color: var(--ion-color-medium);
  font-weight: 500;
}

.metadata-value {
  color: var(--ion-color-dark);
  font-weight: 600;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chip-icon {
  font-size: 14px;
  margin-right: 4px;
}

.empty-state h4 {
  color: var(--ion-color-dark);
  margin: 0 0 8px 0;
  font-weight: 600;
}

.items-card {
  margin: 0;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-icon {
  margin-right: 8px;
  font-size: 20px;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-card {
  padding: 16px;
  border: 1px solid var(--ion-color-light-shade);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.item-card:hover {
  background: var(--ion-color-light-tint);
  border-color: var(--ion-color-primary-tint);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.item-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--ion-color-dark);
}

.item-description {
  margin: 8px 0;
  font-size: 14px;
  color: var(--ion-color-medium-shade);
  line-height: 1.4;
}

.item-meta {
  display: flex;
  gap: 16px;
  margin: 8px 0;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--ion-color-medium);
}

.meta-icon {
  font-size: 14px;
}

.empty-items {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 32px 16px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: var(--ion-color-medium);
}

.empty-items h3 {
  margin: 0;
  color: var(--ion-color-dark);
}

.empty-items p {
  margin: 0;
  color: var(--ion-color-medium);
  font-size: 14px;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 16px;
  }

  .location-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 12px;
  }

  .quick-actions {
    align-self: flex-end;
    margin-top: -12px;
  }

  .location-info {
    gap: 6px;
  }

  .metadata-grid {
    grid-template-columns: 1fr;
  }

  .item-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .header-content {
    gap: 8px;
  }

  .location-info {
    gap: 4px;
  }
}

@media (min-width: 768px) {
  .metadata-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
}
</style>
