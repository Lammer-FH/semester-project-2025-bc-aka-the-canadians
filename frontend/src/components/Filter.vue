<template>
  <div class="filter-container">
    <ion-button
      fill="outline"
      size="small"
      class="filter-button"
      @click="toggleFilterModal"
    >
      <ion-icon :icon="funnelOutline" slot="start"></ion-icon>
      Filter
      <ion-badge v-if="activeFiltersCount > 0" class="filter-badge">
        {{ activeFiltersCount }}
      </ion-badge>
    </ion-button>

    <div v-if="activeFiltersCount > 0" class="filter-chips">
      <ion-chip
        v-for="activeFilter in activeFilters"
        :key="activeFilter.key"
        class="filter-chip"
        @click="clearFilter(activeFilter.key)"
      >
        <ion-icon :icon="activeFilter.icon"></ion-icon>
        <ion-label>{{ activeFilter.label }}</ion-label>
        <ion-icon :icon="closeOutline"></ion-icon>
      </ion-chip>
    </div>

    <ion-modal
      :is-open="showFilterModal"
      @didDismiss="handleFilterModalDismiss"
      :backdrop-dismiss="true"
      :can-dismiss="true"
      :keyboard-close="true"
    >
      <ion-header>
        <ion-toolbar>
          <ion-title>{{ modalTitle }}</ion-title>
          <ion-button slot="end" fill="clear" @click="handleFilterModalDismiss">
            <ion-icon :icon="closeOutline"></ion-icon>
          </ion-button>
        </ion-toolbar>
      </ion-header>
      <ion-content class="filter-modal-content">
        <div
          v-for="filterConfig in filterConfigs"
          :key="filterConfig.key"
          class="filter-section"
        >
          <h3>{{ filterConfig.title }}</h3>

          <ion-radio-group
            v-if="filterConfig.type === 'radio'"
            :model-value="filters[filterConfig.key]"
            @ionChange="updateFilter(filterConfig.key, $event.detail.value)"
          >
            <ion-radio
              v-for="option in filterConfig.options"
              :key="option.value"
              :value="option.value"
              class="filter-radio"
            >
              {{ option.label }}
            </ion-radio>
          </ion-radio-group>

          <ion-radio-group
            v-else-if="filterConfig.type === 'select'"
            :model-value="filters[filterConfig.key]"
            @ionChange="updateFilter(filterConfig.key, $event.detail.value)"
          >
            <ion-radio
              v-for="option in filterConfig.options"
              :key="option.value"
              :value="option.value"
              class="filter-radio"
            >
              {{ option.label }}
            </ion-radio>
          </ion-radio-group>
        </div>

        <div class="filter-actions">
          <ion-button expand="block" @click="applyFilters">
            Apply Filters
          </ion-button>
        </div>
      </ion-content>
    </ion-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import {
  IonButton,
  IonIcon,
  IonBadge,
  IonChip,
  IonLabel,
  IonModal,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonRadioGroup,
  IonRadio,
} from "@ionic/vue";
import { funnelOutline, closeOutline } from "ionicons/icons";

interface FilterOption {
  value: any;
  label: string;
}

interface FilterConfig {
  key: string;
  title: string;
  type: "radio" | "select";
  placeholder?: string;
  options: FilterOption[];
  icon: string;
  getLabel: (value: any) => string;
}

interface ActiveFilter {
  key: string;
  label: string;
  icon: string;
}

interface Props {
  modalTitle: string;
  filterConfigs: FilterConfig[];
  filters: Record<string, any>;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  updateFilter: [key: string, value: any];
  clearFilter: [key: string];
  applyFilters: [];
}>();

const showFilterModal = ref(false);

const activeFiltersCount = computed(() => {
  return Object.values(props.filters).filter(value => {
    if (value === null || value === undefined || value === "") return false;
    return true;
  }).length;
});

const activeFilters = computed((): ActiveFilter[] => {
  const active: ActiveFilter[] = [];

  Object.entries(props.filters).forEach(([key, value]) => {
    if (value === null || value === undefined || value === "") return;

    const config = props.filterConfigs.find(c => c.key === key);
    if (config) {
      active.push({
        key,
        label: config.getLabel(value),
        icon: config.icon,
      });
    }
  });

  return active;
});

const toggleFilterModal = (): void => {
  showFilterModal.value = !showFilterModal.value;
};

const handleFilterModalDismiss = (): void => {
  showFilterModal.value = false;
};

const updateFilter = (key: string, value: any): void => {
  emit("updateFilter", key, value);
};

const clearFilter = (key: string): void => {
  emit("clearFilter", key);
};

const applyFilters = (): void => {
  showFilterModal.value = false;
  emit("applyFilters");
};
</script>

<style scoped>
.filter-container {
  display: contents;
}

.filter-button {
  --border-radius: 12px;
  position: relative;
}

.filter-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  font-size: 10px;
}

.filter-chips {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filter-chip {
  --background: var(--ion-color-primary-tint);
  cursor: pointer;
}

.filter-modal-content {
  padding: 16px;
}

.filter-section {
  margin-bottom: 24px;
  padding: 0 16px;
}

.filter-section h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--ion-color-dark);
}

.filter-radio {
  display: block;
  margin: 12px 0;
  padding: 12px 16px;
  background: var(--ion-color-light-tint);
  border-radius: 8px;
  transition: all 0.2s ease;
  font-size: 16px;
  font-weight: 500;
  color: var(--ion-color-dark);
  border: 2px solid transparent;
}

.filter-radio:hover {
  background: var(--ion-color-light);
}

.filter-radio.radio-checked {
  background: rgba(var(--ion-color-primary-rgb), 0.1);
  border-color: var(--ion-color-primary);
  color: var(--ion-color-primary);
}

.filter-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 32px;
  padding: 0 16px;
}

.filter-actions ion-button {
  --border-radius: 12px;
  height: 48px;
  font-weight: 600;
}

ion-radio-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

@media (max-width: 768px) {
  .filter-radio {
    padding: 16px;
    margin: 8px 0;
    font-size: 16px;
  }

  .filter-section {
    padding: 0 12px;
  }

  .filter-actions {
    padding: 0 12px;
  }
}
</style>
