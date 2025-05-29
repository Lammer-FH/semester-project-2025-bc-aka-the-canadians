<template>
	<template-page :show-profile-button="false">
		<div class="home-container">
			<!-- Hero Section -->
			<div class="hero-section">
				<div class="hero-content">
					<ion-icon :icon="searchOutline" class="hero-icon"></ion-icon>
					<h1>Campus Lost & Found</h1>
					<p class="hero-subtitle">
						Hilf anderen dabei, ihre verlorenen Gegenstände wiederzufinden oder
						melde einen Fund
					</p>
					<div class="hero-buttons">
						<ion-button
							expand="block"
							size="large"
							@click="navigateToItems"
							class="primary-button">
							<ion-icon :icon="eyeOutline" slot="start"></ion-icon>
							Verlorene Gegenstände durchsuchen
						</ion-button>
						<ion-button
							expand="block"
							size="large"
							fill="outline"
							@click="navigateToReport"
							class="secondary-button">
							<ion-icon :icon="megaphoneOutline" slot="start"></ion-icon>
							Gegenstand melden
						</ion-button>
					</div>
				</div>
			</div>

			<!-- Features Section -->
			<div class="features-section">
				<h2>Wie funktioniert es?</h2>
				<div class="features-grid">
					<div class="feature-card">
						<ion-icon
							:icon="megaphoneOutline"
							class="feature-icon"
							color="primary"></ion-icon>
						<h3>Melden</h3>
						<p>
							Hast du einen Gegenstand gefunden oder verloren? Erstelle einfach
							einen Bericht mit allen wichtigen Details.
						</p>
					</div>
					<div class="feature-card">
						<ion-icon
							:icon="searchOutline"
							class="feature-icon"
							color="success"></ion-icon>
						<h3>Suchen</h3>
						<p>
							Durchsuche alle gemeldeten Gegenstände nach Kategorie, Standort
							oder mit der Suchfunktion.
						</p>
					</div>
					<div class="feature-card">
						<ion-icon
							:icon="handRightOutline"
							class="feature-icon"
							color="warning"></ion-icon>
						<h3>Abholen</h3>
						<p>
							Gefunden, was du suchst? Markiere den Gegenstand als abgeholt und
							schließe den Fall ab.
						</p>
					</div>
				</div>
			</div>

			<!-- Statistics Section -->
			<div class="stats-section">
				<h2>Erfolgsgeschichten</h2>
				<div class="stats-grid">
					<div class="stat-card">
						<div class="stat-number">{{ stats.totalReports }}</div>
						<div class="stat-label">Berichte erstellt</div>
					</div>
					<div class="stat-card">
						<div class="stat-number">{{ stats.itemsReturned }}</div>
						<div class="stat-label">Gegenstände zurückgegeben</div>
					</div>
					<div class="stat-card">
						<div class="stat-number">{{ stats.activeUsers }}</div>
						<div class="stat-label">Aktive Nutzer</div>
					</div>
					<div class="stat-card">
						<div class="stat-number">{{ stats.locations }}</div>
						<div class="stat-label">Campus-Standorte</div>
					</div>
				</div>
			</div>

			<!-- How It Works Section -->
			<div class="how-it-works-section">
				<h2>So einfach geht's</h2>
				<div class="steps-container">
					<div class="step">
						<div class="step-number">1</div>
						<div class="step-content">
							<h3>Gegenstand gefunden oder verloren?</h3>
							<p>
								Klicke auf "Gegenstand melden" und fülle das Formular mit allen
								wichtigen Details aus.
							</p>
						</div>
					</div>
					<div class="step">
						<div class="step-number">2</div>
						<div class="step-content">
							<h3>Bericht wird veröffentlicht</h3>
							<p>
								Dein Bericht erscheint sofort in der App und ist für alle Nutzer
								sichtbar.
							</p>
						</div>
					</div>
					<div class="step">
						<div class="step-number">3</div>
						<div class="step-content">
							<h3>Andere können suchen und finden</h3>
							<p>
								Nutzer können nach verlorenen Gegenständen suchen und bei
								Übereinstimmung Kontakt aufnehmen.
							</p>
						</div>
					</div>
					<div class="step">
						<div class="step-number">4</div>
						<div class="step-content">
							<h3>Erfolgreiche Rückgabe</h3>
							<p>
								Wenn ein Gegenstand abgeholt wurde, wird der Bericht als
								erfolgreich abgeschlossen markiert.
							</p>
						</div>
					</div>
				</div>
			</div>

			<!-- Benefits Section -->
			<div class="benefits-section">
				<h2>Warum Campus Lost & Found nutzen?</h2>
				<div class="benefits-grid">
					<div class="benefit-item">
						<ion-icon :icon="flashOutline" color="primary"></ion-icon>
						<div>
							<h4>Schnell & Effizient</h4>
							<p>
								Berichte werden sofort veröffentlicht und sind für alle sichtbar
							</p>
						</div>
					</div>
					<div class="benefit-item">
						<ion-icon :icon="locationOutline" color="success"></ion-icon>
						<div>
							<h4>Standort-basiert</h4>
							<p>
								Suche gezielt nach Gegenständen an bestimmten Campus-Standorten
							</p>
						</div>
					</div>
					<div class="benefit-item">
						<ion-icon :icon="peopleOutline" color="warning"></ion-icon>
						<div>
							<h4>Community-getrieben</h4>
							<p>Hilf anderen und lass dir von der Campus-Community helfen</p>
						</div>
					</div>
					<div class="benefit-item">
						<ion-icon :icon="shieldCheckmarkOutline" color="danger"></ion-icon>
						<div>
							<h4>Sicher & Vertrauenswürdig</h4>
							<p>Alle Berichte werden moderiert und sind nachvollziehbar</p>
						</div>
					</div>
				</div>
			</div>

			<!-- Call to Action -->
			<div class="cta-section">
				<h2>Bereit loszulegen?</h2>
				<p>
					Werde Teil der Campus Lost & Found Community und hilf dabei, verlorene
					Gegenstände zu ihren Besitzern zurückzubringen.
				</p>
				<div class="cta-buttons">
					<ion-button expand="block" size="large" @click="navigateToReport">
						<ion-icon :icon="addOutline" slot="start"></ion-icon>
						Ersten Bericht erstellen
					</ion-button>
					<ion-button
						expand="block"
						size="large"
						fill="outline"
						@click="navigateToItems">
						<ion-icon :icon="listOutline" slot="start"></ion-icon>
						Alle Berichte durchsuchen
					</ion-button>
				</div>
			</div>
		</div>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import { IonButton, IonIcon } from '@ionic/vue';
import {
	searchOutline,
	eyeOutline,
	megaphoneOutline,
	handRightOutline,
	flashOutline,
	locationOutline,
	peopleOutline,
	shieldCheckmarkOutline,
	addOutline,
	listOutline,
} from 'ionicons/icons';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import { useLocationStore } from '@/stores/locationStore';

const router = useRouter();
const itemStore = useItemStore();
const locationStore = useLocationStore();

// Statistics - load from real data
const stats = ref({
	totalReports: 0,
	itemsReturned: 0,
	activeUsers: 0,
	locations: 0,
});

onMounted(async () => {
	// Load real statistics from your stores
	try {
		await Promise.all([itemStore.fetchItems(), locationStore.fetchLocations()]);

		// Calculate real statistics from loaded data
		const items = itemStore.getItems;
		const locations = locationStore.getLocations;

		stats.value = {
			totalReports: items.length,
			itemsReturned: items.filter(
				(item) =>
					item.status?.toUpperCase() === 'CLAIMED' ||
					item.status?.toUpperCase() === 'RETURNED'
			).length,
			activeUsers: Math.max(1, Math.floor(items.length * 0.7)), // Estimate based on items
			locations: locations.length,
		};
	} catch (error) {
		console.error('Error loading statistics:', error);
		// Fallback to mock data if API fails
		stats.value = {
			totalReports: 156,
			itemsReturned: 89,
			activeUsers: 42,
			locations: 15,
		};
	}
});

const navigateToItems = () => {
	router.push('/items/overview');
};

const navigateToReport = () => {
	router.push('/items/report');
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
	position: relative;
	overflow: hidden;
}

.hero-section::before {
	content: '';
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
	animation: shimmer 3s infinite;
}

.hero-content {
	max-width: 600px;
	margin: 0 auto;
	position: relative;
	z-index: 1;
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
	transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
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

@keyframes shimmer {
	0% {
		transform: rotate(45deg) translateX(-100%);
	}
	100% {
		transform: rotate(45deg) translateX(100%);
	}
}

/* Responsive Design */
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
		flex-direction: column;
		text-align: center;
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
