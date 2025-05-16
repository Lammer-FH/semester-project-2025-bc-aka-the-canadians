<template>
	<template-page addButtonPath="/locations/add">
		<template #header>
			<ion-toolbar>
				<navigation-tabs v-model="activeTab" class="full-width-tabs" />
			</ion-toolbar>
			<ion-toolbar>
				<ion-searchbar v-model="searchTerm" placeholder="Search locations..." />
			</ion-toolbar>
		</template>
		<div v-if="activeTab === 'locations'">
			<ion-list>
				<ion-card
					v-for="location in filteredLocations"
					:key="location.id"
					class="ion-margin">
					<ion-card-header>
						<ion-card-title>{{ location.name }}</ion-card-title>
						<ion-card-subtitle>
							Building: {{ location.building }} | Floor: {{ location.floor }} |
							Room: {{ location.room }}
						</ion-card-subtitle>
					</ion-card-header>
					<ion-card-content>
						<p>{{ location.description }}</p>
						<p>
							<strong>Created:</strong>
							{{ new Date(location.createdAt).toLocaleString() }}<br />
							<strong>Updated:</strong>
							{{ new Date(location.updatedAt).toLocaleString() }}
						</p>
					</ion-card-content>
				</ion-card>
			</ion-list>
		</div>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import NavigationTabs from '@/components/NavigationTabs.vue';
import {
	IonSearchbar,
	IonList,
	IonCard,
	IonCardHeader,
	IonCardTitle,
	IonCardSubtitle,
	IonCardContent,
} from '@ionic/vue';
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import type { Location } from '@/models/location';

const router = useRouter();
const activeTab = ref('locations');
const searchTerm = ref('');

// Replace mock data with actual data later on
const locations = ref<Location[]>([
	{
		id: 1,
		name: 'Library',
		description: 'Main campus library',
		building: 'A',
		floor: '1',
		room: '101',
		createdAt: '2024-01-01T10:00:00Z',
		updatedAt: '2024-01-02T12:00:00Z',
	},
	{
		id: 2,
		name: 'Cafeteria',
		description: 'Student cafeteria',
		building: 'B',
		floor: '2',
		room: '201',
		createdAt: '2024-01-03T09:00:00Z',
		updatedAt: '2024-01-04T11:00:00Z',
	},
	{
		id: 3,
		name: 'Gym',
		description: 'Sports and fitness center',
		building: 'C',
		floor: '3',
		room: '301',
		createdAt: '2024-01-05T08:00:00Z',
		updatedAt: '2024-01-06T10:00:00Z',
	},
]);

const filteredLocations = computed(() =>
	locations.value.filter(
		(loc) =>
			loc.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			loc.description.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			loc.building.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			loc.room.toLowerCase().includes(searchTerm.value.toLowerCase())
	)
);

watch(activeTab, (tab) => {
	if (tab === 'items') {
		router.push('/items/home');
	}
});
</script>
