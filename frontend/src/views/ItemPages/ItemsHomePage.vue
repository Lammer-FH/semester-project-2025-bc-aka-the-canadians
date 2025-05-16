<template>
	<template-page addButtonPath="/items/report">
		<template #header>
			<ion-toolbar>
				<navigation-tabs v-model="activeTab" class="full-width-tabs" />
			</ion-toolbar>
			<ion-toolbar>
				<ion-searchbar v-model="searchTerm" placeholder="Search items..." />
			</ion-toolbar>
		</template>
		<div v-if="activeTab === 'items'">
			<ion-list>
				<ion-card
					v-for="item in filteredItems"
					:key="item.id"
					class="ion-margin">
					<ion-card-header>
						<ion-card-title>{{ item.name }}</ion-card-title>
						<ion-card-subtitle>
							Status: {{ item.status }} | Location: {{ item.location }}
						</ion-card-subtitle>
					</ion-card-header>
					<ion-card-content>
						<p>{{ item.description }}</p>
						<p>
							<strong>Created:</strong>
							{{ new Date(item.createdAt).toLocaleString() }}<br />
							<strong>Updated:</strong>
							{{ new Date(item.updatedAt).toLocaleString() }}
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
import type { Item } from '@/models/item';

const router = useRouter();
const activeTab = ref('items');
const searchTerm = ref('');

// Replace mock data with actual data later on
const items = ref<Item[]>([
	{
		id: 1,
		name: 'Backpack',
		description: 'Black backpack with laptop',
		location: 'Library',
		status: 'Lost',
		createdAt: '2024-01-01T10:00:00Z',
		updatedAt: '2024-01-02T12:00:00Z',
	},
	{
		id: 2,
		name: 'Water Bottle',
		description: 'Blue bottle, metal',
		location: 'Cafeteria',
		status: 'Found',
		createdAt: '2024-01-03T09:00:00Z',
		updatedAt: '2024-01-04T11:00:00Z',
	},
	{
		id: 3,
		name: 'Jacket',
		description: 'Red jacket, size M',
		location: 'Gym',
		status: 'Lost',
		createdAt: '2024-01-05T08:00:00Z',
		updatedAt: '2024-01-06T10:00:00Z',
	},
]);

const filteredItems = computed(() =>
	items.value.filter(
		(item) =>
			item.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			item.description.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			item.location.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			item.status.toLowerCase().includes(searchTerm.value.toLowerCase())
	)
);

watch(activeTab, (tab) => {
	if (tab === 'locations') {
		router.push('/locations/home');
	}
});
</script>
