<template>
	<template-page
		:headline="'Location Details'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton">
		<ion-list class="ion-padding">
			<ion-item>
				<ion-label position="stacked">Name</ion-label>
				<ion-input readonly :value="location.name"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Description</ion-label>
				<ion-textarea readonly :value="location.description"></ion-textarea>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Building</ion-label>
				<ion-input readonly :value="location.building"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Floor</ion-label>
				<ion-input readonly :value="location.floor"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Room</ion-label>
				<ion-input readonly :value="location.room"></ion-input>
			</ion-item>
			<!-- Items at location -->
			<ion-item lines="none">
				<ion-label position="stacked"
					><strong>Items at location</strong></ion-label
				>
			</ion-item>
			<ion-list>
				<ion-item v-for="item in itemsAtLocation" :key="item.id">
					<ion-label>
						<strong>{{ item.name }}</strong
						><br />
						<span>{{ item.description }}</span>
					</ion-label>
				</ion-item>
			</ion-list>
		</ion-list>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import { IonList, IonItem, IonLabel, IonInput, IonTextarea } from '@ionic/vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import type { Location } from '@/models/location';

const router = useRouter();

const leftFooterButton = {
	name: 'Delete',
	color: 'danger',
};
const rightFooterButton = {
	name: 'Edit Location',
	color: 'primary',
};

//replace with actual data later on
const location = ref<Location>({
	id: 1,
	name: 'Main Library',
	description: 'Central library building',
	building: 'A',
	floor: '1',
	room: '101',
});

// replace with actual data later on
const itemsAtLocation = ref([
	{ id: 1, name: 'Backpack', description: 'Black backpack with laptop' },
	{ id: 2, name: 'Water Bottle', description: 'Blue metal bottle' },
	{ id: 3, name: 'Jacket', description: 'Red jacket, size M' },
]);
</script>
