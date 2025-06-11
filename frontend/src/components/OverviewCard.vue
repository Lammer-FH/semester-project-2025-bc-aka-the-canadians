<template>
  <ion-card
    class="universal-card"
    :class="cardClasses"
    @click="handleCardClick"
    :style="animationStyle"
  >
    <div class="card-header" :class="headerClasses">
      <div class="header-content">
        <h3 class="card-title">{{ title }}</h3>
        <div v-if="status" class="status-chip" :class="getStatusClass(status)">
          <ion-icon :icon="getStatusIcon(status)" class="chip-icon"></ion-icon>
          <span>{{ getStatusText(status) }}</span>
        </div>
      </div>
    </div>

    <ion-card-content class="card-content">
      <div v-if="description" class="description">
        <p>{{ truncateText(description, descriptionLength) }}</p>
      </div>

      <div
        v-for="section in contentSections"
        :key="section.key"
        class="content-section"
        :class="section.className"
      >
        <h4 v-if="section.title" class="section-title">
          <ion-icon
            v-if="section.icon"
            :icon="section.icon"
            class="section-icon"
          ></ion-icon>
          {{ section.title }}
        </h4>

        <div v-if="section.type === 'stats'" class="stats-display">
          <ion-chip
            v-for="stat in section.data as StatData[]"
            :key="stat.key"
            :color="stat.color"
            class="stat-chip"
          >
            <ion-icon
              v-if="stat.icon"
              :icon="stat.icon"
              class="chip-icon"
            ></ion-icon>
            {{ stat.value }} {{ stat.label }}
          </ion-chip>
        </div>

        <div v-else-if="section.type === 'list'" class="list-display">
          <div
            v-for="(item, index) in (section.data as ListData[]).slice(
              0,
              section.maxItems || 3
            )"
            :key="index"
            class="list-item"
            @click.stop="handleListItemClick(item, section.key)"
          >
            <div class="list-item-content">
              <ion-icon
                v-if="item.icon"
                :icon="item.icon"
                :color="item.color"
                class="list-icon"
              ></ion-icon>
              <div class="list-text">
                <span class="list-title">{{ item.title }}</span>
                <span v-if="item.subtitle" class="list-subtitle">{{
                  item.subtitle
                }}</span>
              </div>
            </div>
          </div>
          <div
            v-if="(section.data as ListData[]).length > (section.maxItems || 3)"
            class="more-items"
          >
            +{{
              (section.data as ListData[]).length - (section.maxItems || 3)
            }}
            more
          </div>
        </div>

        <div v-else-if="section.type === 'text'" class="text-content">
          <p>{{ section.data as string }}</p>
        </div>
      </div>

      <div v-if="details && details.length > 0" class="details-section">
        <div v-for="detail in details" :key="detail.key" class="detail-item">
          <ion-icon
            v-if="detail.icon"
            :icon="detail.icon"
            class="detail-icon"
          ></ion-icon>
          <span>{{ detail.value }}</span>
        </div>
      </div>

      <div v-if="metadata" class="metadata">
        <div v-for="meta in metadata" :key="meta.key" class="metadata-item">
          <ion-icon
            v-if="meta.icon"
            :icon="meta.icon"
            class="metadata-icon"
          ></ion-icon>
          <span class="metadata-text">{{ meta.value }}</span>
        </div>
      </div>
    </ion-card-content>

    <div v-if="actions && actions.length > 0" class="card-actions">
      <ion-button
        v-for="action in actions"
        :key="action.key"
        :fill="action.fill || 'clear'"
        :size="action.size || 'small'"
        :color="action.color"
        @click.stop="handleActionClick(action)"
      >
        <ion-icon
          v-if="action.icon"
          :icon="action.icon"
          slot="start"
        ></ion-icon>
        {{ action.label }}
      </ion-button>
    </div>
  </ion-card>
</template>

<script setup lang="ts">
import { computed } from "vue";
import {
  IonCard,
  IonCardContent,
  IonButton,
  IonIcon,
  IonChip,
} from "@ionic/vue";
import {
  checkmarkCircleOutline,
  closeCircleOutline,
  searchOutline,
  eyeOutline,
  flagOutline,
} from "ionicons/icons";

interface CardAction {
  key: string;
  label: string;
  icon?: string;
  iconSlot?: "start" | "end" | "icon-only";
  fill?: "clear" | "outline" | "solid";
  size?: "small" | "default" | "large";
  color?: string;
  handler: () => void;
}

interface CardDetail {
  key: string;
  icon?: string;
  value: string;
}

interface CardMetadata {
  key: string;
  icon?: string;
  value: string;
}

interface StatData {
  key: string;
  value: string | number;
  label: string;
  color?: string;
  icon?: string;
}

interface ListData {
  title: string;
  subtitle?: string;
  icon?: string;
  color?: string;
  data?: unknown;
}

interface ContentSection {
  key: string;
  title?: string;
  icon?: string;
  type: "stats" | "list" | "text";
  className?: string;
  maxItems?: number;
  data: StatData[] | ListData[] | string;
}

interface Props {
  title: string;
  description?: string;
  descriptionLength?: number;
  status?: string | boolean;
  details?: CardDetail[];
  metadata?: CardMetadata[];
  actions?: CardAction[];
  contentSections?: ContentSection[];
  cardType?: "item" | "location" | "report";
  animationDelay?: number;
  clickable?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  description: "",
  descriptionLength: 120,
  status: "",
  details: () => [],
  metadata: () => [],
  actions: () => [],
  contentSections: () => [],
  cardType: "item",
  animationDelay: 0,
  clickable: true,
});

const emit = defineEmits<{
  cardClick: [];
  actionClick: [action: CardAction];
  listItemClick: [item: unknown, sectionKey: string];
}>();

const cardClasses = computed(() => ({
  [`${props.cardType}-card`]: true,
  clickable: props.clickable,
}));

const headerClasses = computed(() => ({
  [`${props.cardType}-header`]: true,
}));

const animationStyle = computed(() => ({
  "--animation-delay": `${props.animationDelay}s`,
}));

const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + "...";
};

const getStatusClass = (status: string | boolean): string => {
  if (typeof status === "boolean") {
    return status ? "status-active" : "status-inactive";
  }

  switch (status.toLowerCase()) {
    case "claimed":
    case "found":
    case "active":
    case "resolved":
      return "status-success";
    case "unclaimed":
    case "lost":
    case "open":
      return "status-warning";
    case "closed":
    case "inactive":
      return "status-medium";
    default:
      return "status-primary";
  }
};

const getStatusIcon = (status: string | boolean): string => {
  if (typeof status === "boolean") {
    return status ? checkmarkCircleOutline : closeCircleOutline;
  }

  switch (status.toLowerCase()) {
    case "claimed":
    case "found":
    case "resolved":
      return checkmarkCircleOutline;
    case "unclaimed":
    case "lost":
    case "open":
      return searchOutline;
    case "active":
      return eyeOutline;
    case "closed":
    case "inactive":
      return closeCircleOutline;
    default:
      return flagOutline;
  }
};

const getStatusText = (status: string | boolean): string => {
  if (typeof status === "boolean") {
    return status ? "Active" : "Inactive";
  }

  return status.charAt(0).toUpperCase() + status.slice(1);
};

const handleCardClick = (): void => {
  if (props.clickable) {
    emit("cardClick");
  }
};

const handleActionClick = (action: CardAction): void => {
  action.handler();
  emit("actionClick", action);
};

const handleListItemClick = (item: unknown, sectionKey: string): void => {
  emit("listItemClick", item, sectionKey);
};
</script>

<style scoped>
.universal-card {
  transition: all 0.2s ease;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  animation: slideInUp 0.6s ease-out;
  animation-delay: var(--animation-delay);
  animation-fill-mode: backwards;
}

.universal-card.clickable {
  cursor: pointer;
}

.universal-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-header {
  padding: 16px;
  color: white;
  position: relative;
  overflow: hidden;
}

.item-header {
  background: linear-gradient(
    135deg,
    var(--ion-color-primary) 0%,
    var(--ion-color-primary-shade) 100%
  );
}

.location-header {
  background: linear-gradient(
    135deg,
    var(--ion-color-success) 0%,
    var(--ion-color-success-shade) 100%
  );
}

.report-header {
  background: linear-gradient(
    135deg,
    var(--ion-color-warning) 0%,
    var(--ion-color-warning-shade) 100%
  );
}

.card-header::before {
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

.universal-card:hover .card-header::before {
  transform: rotate(45deg) translateX(100%);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  flex: 1;
  margin-right: 12px;
}

.status-chip {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.2);
}

.status-chip.status-success {
  background: var(--ion-color-success);
}

.status-chip.status-warning {
  background: var(--ion-color-warning);
}

.status-chip.status-medium {
  background: var(--ion-color-medium);
}

.status-chip.status-primary {
  background: var(--ion-color-primary);
}

.chip-icon {
  font-size: 16px;
}

.card-content {
  padding: 16px;
}

.description {
  margin-bottom: 16px;
}

.description p {
  margin: 0;
  color: var(--ion-color-medium);
  font-size: 14px;
  line-height: 1.4;
}

.content-section {
  margin-bottom: 16px;
}

.section-title {
  color: var(--ion-color-dark);
  margin: 0 0 12px 0;
  font-size: 1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-icon {
  font-size: 16px;
  color: var(--ion-color-primary);
}

.stats-display {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.stat-chip {
  font-size: 0.8em;
}

.list-display {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.list-item {
  padding: 8px;
  background: var(--ion-color-light-tint);
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.list-item:hover {
  background: var(--ion-color-light-shade);
}

.list-item-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.list-icon {
  font-size: 14px;
}

.list-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.list-title {
  font-size: 0.85em;
  font-weight: 500;
  color: var(--ion-color-dark);
}

.list-subtitle {
  font-size: 0.75em;
  color: var(--ion-color-medium);
}

.more-items {
  font-size: 0.8em;
  color: var(--ion-color-primary);
  font-style: italic;
  padding: 4px 8px;
}

.text-content p {
  margin: 0;
  color: var(--ion-color-medium-shade);
  font-size: 14px;
  line-height: 1.4;
}

.details-section {
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-icon {
  font-size: 16px;
  color: var(--ion-color-medium);
}

.metadata {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--ion-color-medium);
  border-top: 1px solid var(--ion-color-light);
  padding-top: 12px;
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.metadata-icon {
  font-size: 14px;
}

.metadata-text {
  font-size: 12px;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px 16px;
  gap: 8px;
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
  .header-content {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .card-actions {
    flex-direction: column;
    gap: 8px;
  }

  .metadata {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}
</style>
