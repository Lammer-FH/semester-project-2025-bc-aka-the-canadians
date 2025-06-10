<template>
  <template-page :show-profile-button="false">
    <div class="home-container">
      <div class="hero-section">
        <div class="hero-content">
          <ion-icon :icon="searchOutline" class="hero-icon"></ion-icon>
          <h1>Campus Lost & Found</h1>
          <p class="hero-subtitle">
            Find your lost items or report items you've found on campus.
          </p>
          <div class="hero-buttons">
            <ion-button
              expand="block"
              size="large"
              @click="navigateToItems"
              class="primary-button"
            >
              <ion-icon :icon="eyeOutline" slot="start"></ion-icon>
              Browse Lost Items
            </ion-button>
            <ion-button
              expand="block"
              size="large"
              fill="outline"
              @click="navigateToReport"
              class="secondary-button"
            >
              <ion-icon :icon="megaphoneOutline" slot="start"></ion-icon>
              Report Item
            </ion-button>
          </div>
        </div>
      </div>

      <div class="features-section">
        <h2>How does it work?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <ion-icon
              :icon="megaphoneOutline"
              class="feature-icon"
              color="primary"
            ></ion-icon>
            <h3>Report</h3>
            <p>
              Have you found or lost an item? Simply create a report with all
              the important details.
            </p>
          </div>
          <div class="feature-card">
            <ion-icon
              :icon="searchOutline"
              class="feature-icon"
              color="success"
            ></ion-icon>
            <h3>Search</h3>
            <p>
              Browse all reported items by category, location, or using the
              search function.
            </p>
          </div>
          <div class="feature-card">
            <ion-icon
              :icon="handRightOutline"
              class="feature-icon"
              color="warning"
            ></ion-icon>
            <h3>Pickup</h3>
            <p>
              Found what you're looking for? Claim the item and close the case.
            </p>
          </div>
        </div>
      </div>

      <div class="stats-section">
        <h2>Success Stories</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-number">{{ stats.totalReports }}</div>
            <div class="stat-label">Reports Created</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.itemsReturned }}</div>
            <div class="stat-label">Items Returned</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.locations }}</div>
            <div class="stat-label">Known Locations</div>
          </div>
        </div>
      </div>

      <div class="how-it-works-section">
        <h2>It's that simple</h2>
        <div class="steps-container">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3>Found or lost an item?</h3>
              <p>
                Click on "Report Item" and fill out the form with all the
                important details.
              </p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3>Report gets published</h3>
              <p>
                Your report appears immediately in the app and is visible to all
                users.
              </p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3>Others can search and find</h3>
              <p>
                Users can search for lost items and make contact when there's a
                match.
              </p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">4</div>
            <div class="step-content">
              <h3>Successful return</h3>
              <p>
                When an item is picked up, the report is marked as successfully
                completed.
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="benefits-section">
        <h2>Why use Campus Lost & Found?</h2>
        <div class="benefits-grid">
          <div class="benefit-item">
            <ion-icon :icon="flashOutline" color="primary"></ion-icon>
            <div>
              <h4>Fast & Efficient</h4>
              <p>
                Reports are published immediately and are visible to everyone
              </p>
            </div>
          </div>
          <div class="benefit-item">
            <ion-icon :icon="locationOutline" color="success"></ion-icon>
            <div>
              <h4>Location-based</h4>
              <p>Search specifically for items at certain campus locations</p>
            </div>
          </div>
          <div class="benefit-item">
            <ion-icon :icon="peopleOutline" color="warning"></ion-icon>
            <div>
              <h4>Community-driven</h4>
              <p>Help others and get help from the campus community</p>
            </div>
          </div>
          <div class="benefit-item">
            <ion-icon :icon="shieldCheckmarkOutline" color="danger"></ion-icon>
            <div>
              <h4>Safe & Trustworthy</h4>
              <p>All reports are moderated and traceable</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import { IonButton, IonIcon } from "@ionic/vue";
import {
  searchOutline,
  eyeOutline,
  megaphoneOutline,
  handRightOutline,
  flashOutline,
  locationOutline,
  peopleOutline,
  shieldCheckmarkOutline,
} from "ionicons/icons";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useItemStore } from "@/stores/itemStore";
import { useLocationStore } from "@/stores/locationStore";

const router = useRouter();
const itemStore = useItemStore();
const locationStore = useLocationStore();

const stats = ref({
  totalReports: 0,
  itemsReturned: 0,
  locations: 0,
});

onMounted(async () => {
  try {
    await Promise.all([itemStore.fetchItems(), locationStore.fetchLocations()]);

    const items = itemStore.getItems;
    const locations = locationStore.getLocations;

    stats.value = {
      totalReports: items.length,
      itemsReturned: items.filter(
        item =>
          item.status?.toUpperCase() === "CLAIMED" ||
          item.status?.toUpperCase() === "RETURNED"
      ).length,
      locations: locations.length,
    };
  } catch (error) {
    console.error("Error loading statistics:", error);
    stats.value = {
      totalReports: 0,
      itemsReturned: 0,
      locations: 0,
    };
  }
});

const navigateToItems = () => {
  router.push("/items/overview");
};

const navigateToReport = () => {
  router.push("/items/report");
};
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--ion-color-light-tint), white);
}

.hero-section {
  background: linear-gradient(
    135deg,
    var(--ion-color-primary),
    var(--ion-color-primary-shade)
  );
  color: white;
  padding: 60px 20px;
  text-align: center;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
}

.hero-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.9;
}

.hero-section h1 {
  font-size: 2.5em;
  font-weight: 700;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-subtitle {
  font-size: 1.2em;
  margin: 0 0 40px 0;
  opacity: 0.9;
  line-height: 1.4;
}

.hero-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 400px;
  margin: 0 auto;
}

.primary-button {
  --background: rgba(255, 255, 255, 0.15);
  --color: white;
  --border-color: rgba(255, 255, 255, 0.3);
  --border-width: 2px;
  --border-style: solid;
  font-weight: 600;
}

.secondary-button {
  --border-color: rgba(255, 255, 255, 0.5);
  --color: white;
  font-weight: 600;
}

.features-section,
.stats-section,
.how-it-works-section,
.benefits-section,
.cta-section {
  padding: 60px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.features-section h2,
.stats-section h2,
.how-it-works-section h2,
.benefits-section h2,
.cta-section h2 {
  text-align: center;
  font-size: 2em;
  font-weight: 700;
  margin: 0 0 40px 0;
  color: var(--ion-color-dark);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.feature-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 1.3em;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: var(--ion-color-dark);
}

.feature-card p {
  color: var(--ion-color-medium-shade);
  line-height: 1.6;
  margin: 0;
}

.stats-section {
  background: var(--ion-color-light-tint);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 2.5em;
  font-weight: 700;
  color: var(--ion-color-primary);
  margin-bottom: 8px;
}

.stat-label {
  color: var(--ion-color-medium);
  font-weight: 500;
}

.steps-container {
  max-width: 800px;
  margin: 0 auto;
}

.step {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 40px;
}

.step-number {
  width: 50px;
  height: 50px;
  background: var(--ion-color-primary);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.2em;
  flex-shrink: 0;
}

.step-content h3 {
  font-size: 1.2em;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--ion-color-dark);
}

.step-content p {
  color: var(--ion-color-medium-shade);
  line-height: 1.6;
  margin: 0;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.benefit-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.benefit-item ion-icon {
  font-size: 24px;
  flex-shrink: 0;
  margin-top: 4px;
}

.benefit-item h4 {
  font-size: 1.1em;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--ion-color-dark);
}

.benefit-item p {
  color: var(--ion-color-medium-shade);
  line-height: 1.5;
  margin: 0;
}

.cta-section {
  background: linear-gradient(
    135deg,
    var(--ion-color-primary-tint),
    var(--ion-color-primary)
  );
  color: white;
  text-align: center;
  border-radius: 16px;
  margin: 0 20px 40px;
}

.cta-section h2 {
  color: white;
}

.cta-section p {
  font-size: 1.1em;
  margin: 0 0 40px 0;
  opacity: 0.9;
  line-height: 1.6;
}

.cta-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 400px;
  margin: 0 auto;
}

.cta-buttons ion-button {
  font-weight: 600;
}

.cta-buttons ion-button:first-child {
  --background: rgba(255, 255, 255, 0.15);
  --color: white;
  --border-color: rgba(255, 255, 255, 0.3);
  --border-width: 2px;
  --border-style: solid;
}

.cta-buttons ion-button:last-child {
  --border-color: rgba(255, 255, 255, 0.5);
  --color: white;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 20px;
  }

  .hero-section h1 {
    font-size: 2em;
  }

  .hero-subtitle {
    font-size: 1em;
  }

  .hero-icon {
    font-size: 60px;
  }

  .features-section,
  .stats-section,
  .how-it-works-section,
  .benefits-section,
  .cta-section {
    padding: 40px 20px;
  }

  .features-grid,
  .benefits-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .step {
    gap: 16px;
  }

  .step-number {
    width: 40px;
    height: 40px;
    font-size: 1em;
  }
}

@media (max-width: 480px) {
  .hero-buttons,
  .cta-buttons {
    gap: 12px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .benefit-item {
    flex-direction: column;
    text-align: center;
  }
}
</style>
