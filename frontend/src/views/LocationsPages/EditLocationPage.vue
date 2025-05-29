<template>
	<template-page
		:headline="'Standort bearbeiten'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleCancel"
		@rightFooterButtonClicked="handleSave">
		<div class="form-container">
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Standort...</p>
			</div>

			<div v-else-if="error && !location.name" class="empty-state">
				<ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
				<h2>Fehler beim Laden</h2>
				<p>{{ error }}</p>
				<ion-button @click="loadLocation">
					<ion-icon :icon="refreshOutline" slot="start"></ion-icon>
					Erneut versuchen
				</ion-button>
			</div>

			<div v-else class="form-content">
				<div class="form-header">
					<ion-icon :icon="createOutline" class="header-icon"></ion-icon>
					<h2>Standort bearbeiten</h2>
					<p>Aktualisiere die Details für diesen Standort</p>
					<div v-if="location.updatedAt" class="last-modified">
						<ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
						Zuletzt bearbeitet: {{ formatDate(location.updatedAt) }}
					</div>
				</div>

				<div class="input-group">
					<ion-input
						v-model="location.name"
						label="Name des Standorts *"
						label-placement="stacked"
						placeholder="z.B. Hauptbibliothek, Mensa Nord, Hörsaal A"
						class="modern-input"
						:class="{
							'input-filled': location.name,
							'input-error': errors.name,
						}"
						@ionBlur="validateField('name')">
						<ion-icon
							:icon="textOutline"
							slot="start"
							class="input-icon"></ion-icon>
					</ion-input>
					<div v-if="errors.name" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.name }}
					</div>
				</div>

				<div class="input-group">
					<ion-textarea
						v-model="location.description"
						label="Beschreibung"
						label-placement="stacked"
						placeholder="Detaillierte Beschreibung des Standorts und seiner Besonderheiten..."
						class="modern-textarea"
						:class="{
							'textarea-filled': location.description,
						}"
						:auto-grow="true"
						rows="3">
						<ion-icon
							:icon="documentTextOutline"
							slot="start"
							class="input-icon"></ion-icon>
					</ion-textarea>
				</div>

				<div class="input-group">
					<ion-input
						v-model="location.building"
						label="Gebäude *"
						label-placement="stacked"
						placeholder="z.B. Hauptgebäude, Neubau, Labor"
						class="modern-input"
						:class="{
							'input-filled': location.building,
							'input-error': errors.building,
						}"
						@ionBlur="validateField('building')">
						<ion-icon
							:icon="businessOutline"
							slot="start"
							class="input-icon"></ion-icon>
					</ion-input>
					<div v-if="errors.building" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.building }}
					</div>
				</div>

				<div class="input-row">
					<div class="input-group half-width">
						<ion-input
							v-model="location.floor"
							label="Etage *"
							label-placement="stacked"
							placeholder="z.B. EG, 1, 2, UG"
							class="modern-input"
							:class="{
								'input-filled': location.floor,
								'input-error': errors.floor,
							}"
							@ionBlur="validateField('floor')">
							<ion-icon
								:icon="layersOutline"
								slot="start"
								class="input-icon"></ion-icon>
						</ion-input>
						<div v-if="errors.floor" class="error-message">
							<ion-icon :icon="alertCircleOutline"></ion-icon>
							{{ errors.floor }}
						</div>
					</div>

					<div class="input-group half-width">
						<ion-input
							v-model="location.room"
							label="Raum *"
							label-placement="stacked"
							placeholder="z.B. 101, A-15, Foyer"
							class="modern-input"
							:class="{
								'input-filled': location.room,
								'input-error': errors.room,
							}"
							@ionBlur="validateField('room')">
							<ion-icon
								:icon="homeOutline"
								slot="start"
								class="input-icon"></ion-icon>
						</ion-input>
						<div v-if="errors.room" class="error-message">
							<ion-icon :icon="alertCircleOutline"></ion-icon>
							{{ errors.room }}
						</div>
					</div>
				</div>

				<div class="action-buttons">
					<ion-button
						fill="outline"
						color="danger"
						expand="block"
						class="delete-button"
						@click="handleDelete">
						<ion-icon :icon="trashOutline" slot="start"></ion-icon>
						Standort löschen
					</ion-button>
				</div>

				<div class="form-footer-info">
					<div class="info-item">
						<ion-icon
							:icon="informationCircleOutline"
							color="primary"
							class="info-icon"></ion-icon>
						<div class="info-text">
							<p>
								Alle Änderungen werden automatisch gespeichert, wenn du auf
								"Änderungen speichern" klickst.
							</p>
							<p>Mit * markierte Felder sind Pflichtfelder.</p>
						</div>
					</div>
				</div>
			</div>
		</div>

		<ion-alert
			:is-open="showDeleteAlert"
			header="Standort löschen"
			message="Bist du sicher, dass du diesen Standort löschen möchtest? Diese Aktion kann nicht rückgängig gemacht werden."
			:buttons="alertButtons"
			@didDismiss="showDeleteAlert = false"></ion-alert>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import {
	IonInput,
	IonTextarea,
	IonIcon,
	IonButton,
	IonSpinner,
	IonAlert,
} from '@ionic/vue';
import {
	createOutline,
	textOutline,
	documentTextOutline,
	businessOutline,
	layersOutline,
	alertCircleOutline,
	informationCircleOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
	trashOutline,
	timeOutline,
	refreshOutline,
	homeOutline,
} from 'ionicons/icons';
import { ref, computed, watch, onMounted, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useLocationStore } from '@/stores/locationStore';
import type { Location } from '@/models/location';

const router = useRouter();
const route = useRoute();
const locationStore = useLocationStore();

const location = ref<Location>({
	id: 0,
	name: '',
	description: '',
	building: '',
	floor: '',
	room: '',
	createdAt: '',
	updatedAt: '',
});

const errors = ref({
	name: '',
	building: '',
	floor: '',
	room: '',
});

const isLoading = ref(false);
const error = ref<string | null>(null);
const isSaving = ref(false);
const showDeleteAlert = ref(false);

const leftFooterButton = computed(() => ({
	name: 'Abbrechen',
	color: 'medium',
	icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
	name: isSaving.value
		? 'Speichere...'
		: isValid.value
		? 'Änderungen speichern'
		: 'Felder ausfüllen',
	color: isValid.value ? 'primary' : 'medium',
	icon: checkmarkCircleOutline,
	disabled: !isValid.value || isSaving.value || isLoading.value,
}));

const isValid = computed(() => {
	try {
		return (
			location.value?.name?.trim() !== '' &&
			location.value?.building?.trim() !== '' &&
			location.value?.floor?.trim() !== '' &&
			location.value?.room?.trim() !== '' &&
			Object.values(errors.value).every((error) => error === '')
		);
	} catch (error) {
		console.error('Error in isValid computed:', error);
		return false;
	}
});

const alertButtons = [
	{
		text: 'Abbrechen',
		role: 'cancel',
		cssClass: 'alert-button-cancel',
	},
	{
		text: 'Löschen',
		role: 'destructive',
		cssClass: 'alert-button-confirm',
		handler: () => confirmDelete(),
	},
];

const formatDate = (dateString: string) => {
	try {
		return new Date(dateString).toLocaleDateString('de-DE', {
			day: '2-digit',
			month: '2-digit',
			year: 'numeric',
			hour: '2-digit',
			minute: '2-digit',
		});
	} catch (error) {
		console.error('Error formatting date:', error);
		return 'Ungültiges Datum';
	}
};

const validateField = (fieldName: keyof typeof errors.value) => {
	try {
		const value = String(
			location.value?.[fieldName as keyof typeof location.value] || ''
		).trim();

		switch (fieldName) {
			case 'name':
				if (!value) {
					errors.value.name = 'Name ist erforderlich';
				} else if (value.length < 2) {
					errors.value.name = 'Name muss mindestens 2 Zeichen haben';
				} else {
					errors.value.name = '';
				}
				break;
			case 'building':
				if (!value) {
					errors.value.building = 'Gebäude ist erforderlich';
				} else {
					errors.value.building = '';
				}
				break;
			case 'floor':
				if (!value) {
					errors.value.floor = 'Etage ist erforderlich';
				} else {
					errors.value.floor = '';
				}
				break;
			case 'room':
				if (!value) {
					errors.value.room = 'Raum ist erforderlich';
				} else {
					errors.value.room = '';
				}
				break;
		}
	} catch (error) {
		console.error('Error validating field:', fieldName, error);
	}
};

const validateAllFields = () => {
	try {
		validateField('name');
		validateField('building');
		validateField('floor');
		validateField('room');
	} catch (error) {
		console.error('Error validating all fields:', error);
	}
};

const loadLocation = async () => {
	try {
		isLoading.value = true;
		error.value = null;

		const locationId = Number(route.params.id);
		if (!locationId || isNaN(locationId)) {
			throw new Error('Invalid location ID');
		}

		await locationStore.fetchLocationById(locationId);
		const loadedLocation = locationStore.getCurrentLocation;

		if (loadedLocation) {
			await nextTick();
			location.value = { ...loadedLocation };
		} else {
			throw new Error('Location not found');
		}
	} catch (err) {
		console.error('Error loading location:', err);
		error.value = err instanceof Error ? err.message : 'Unknown error';
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
		console.error('Error navigating back:', error);
		router.push('/locations/overview');
	}
};

const handleSave = async () => {
	if (!isValid.value) {
		validateAllFields();
		return;
	}

	try {
		isSaving.value = true;

		const updateData = {
			name: location.value.name.trim(),
			description: location.value.description?.trim() || '',
			building: location.value.building.trim(),
			floor: location.value.floor.trim(),
			room: location.value.room.trim(),
		};

		await locationStore.updateLocation(location.value.id, updateData);

		router.push(`/locations/${location.value.id}`);
	} catch (error) {
		console.error('Error saving location:', error);
	} finally {
		isSaving.value = false;
	}
};

const handleDelete = () => {
	showDeleteAlert.value = true;
};

const confirmDelete = async () => {
	try {
		await locationStore.deleteLocation(location.value.id);
		router.push('/locations/overview');
	} catch (error) {
		console.error('Error deleting location:', error);
	}
};

watch(
	() => location.value?.name,
	(newName) => {
		try {
			if (newName && errors.value.name) {
				errors.value.name = '';
			}
		} catch (error) {
			console.error('Error in name watcher:', error);
		}
	}
);

watch(
	() => location.value?.building,
	(newBuilding) => {
		try {
			if (newBuilding && errors.value.building) {
				errors.value.building = '';
			}
		} catch (error) {
			console.error('Error in building watcher:', error);
		}
	}
);

watch(
	() => location.value?.floor,
	(newFloor) => {
		try {
			if (newFloor && errors.value.floor) {
				errors.value.floor = '';
			}
		} catch (error) {
			console.error('Error in floor watcher:', error);
		}
	}
);

watch(
	() => location.value?.room,
	(newRoom) => {
		try {
			if (newRoom && errors.value.room) {
				errors.value.room = '';
			}
		} catch (error) {
			console.error('Error in room watcher:', error);
		}
	}
);

onMounted(async () => {
	try {
		await loadLocation();
	} catch (error) {
		console.error('Error in onMounted:', error);
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

.input-row {
	display: flex;
	gap: 16px;
}

.half-width {
	flex: 1;
}

.modern-input,
.modern-textarea {
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
.modern-textarea.textarea-filled {
	border-color: var(--ion-color-primary-tint);
	--background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-input.input-error,
.modern-textarea.textarea-error {
	border-color: var(--ion-color-danger);
	--background: rgba(var(--ion-color-danger-rgb), 0.05);
}

.modern-input:focus-within,
.modern-textarea:focus-within {
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

.action-buttons {
	margin: 40px 0;
	padding: 20px;
	border-top: 1px solid var(--ion-color-light-shade);
	border-bottom: 1px solid var(--ion-color-light-shade);
}

.delete-button {
	width: 100%;
	--border-radius: 12px;
	height: 48px;
	font-weight: 600;
}

.form-footer-info {
	margin-top: 20px;
	padding: 20px;
	background: linear-gradient(
		135deg,
		var(--ion-color-light-tint),
		var(--ion-color-light)
	);
	border-radius: 12px;
	border-left: 4px solid var(--ion-color-primary);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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

.info-text p {
	margin: 0 0 8px 0;
	color: var(--ion-color-medium-shade);
	font-size: 0.9em;
	line-height: 1.4;
}

.info-text p:last-child {
	margin-bottom: 0;
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

	.input-row {
		flex-direction: column;
		gap: 0;
	}

	.half-width {
		flex: none;
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
