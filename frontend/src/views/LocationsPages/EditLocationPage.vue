<template>
	<template-page
		:headline="'Standort bearbeiten'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleCancel"
		@rightFooterButtonClicked="handleSave">
		
		<div class="form-container">
			<!-- Loading State -->
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Standort-Daten...</p>
			</div>

			<div v-else>
				<!-- Form Header -->
				<div class="form-header">
					<ion-icon :icon="createOutline" class="header-icon"></ion-icon>
					<h2>Standort bearbeiten</h2>
					<p>Aktualisiere die Details für diesen Standort</p>
					
					<!-- Last Modified Info -->
					<div v-if="location.updatedAt" class="last-modified">
						<ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
						<span>Zuletzt geändert: {{ formatDate(location.updatedAt) }}</span>
					</div>
				</div>

				<!-- Form Content -->
				<div class="form-content">
					<!-- Name Field -->
					<div class="input-group">
						<ion-item 
							class="modern-item" 
							:class="{ 'item-error': errors.name, 'item-filled': location.name }"
						>
							<ion-label position="stacked" class="custom-label">
								<ion-icon :icon="textOutline" class="label-icon"></ion-icon>
								Standort-Name *
							</ion-label>
							<ion-input
								v-model="location.name"
								placeholder="z.B. Hauptbibliothek"
								@ionBlur="validateField('name')"
								:class="{ 'input-error': errors.name }"
							></ion-input>
						</ion-item>
						<div v-if="errors.name" class="error-message">
							<ion-icon :icon="alertCircleOutline"></ion-icon>
							{{ errors.name }}
						</div>
					</div>

					<!-- Description Field -->
					<div class="input-group">
						<ion-item 
							class="modern-item textarea-item" 
							:class="{ 'item-filled': location.description }"
						>
							<ion-label position="stacked" class="custom-label">
								<ion-icon :icon="documentTextOutline" class="label-icon"></ion-icon>
								Beschreibung
							</ion-label>
							<ion-textarea
								v-model="location.description"
								placeholder="Kurze Beschreibung des Standorts..."
								:rows="3"
								:auto-grow="true"
								class="custom-textarea"
							></ion-textarea>
						</ion-item>
					</div>

					<!-- Building Field -->
					<div class="input-group">
						<ion-item 
							class="modern-item" 
							:class="{ 'item-error': errors.building, 'item-filled': location.building }"
						>
							<ion-label position="stacked" class="custom-label">
								<ion-icon :icon="businessOutline" class="label-icon"></ion-icon>
								Gebäude *
							</ion-label>
							<ion-input
								v-model="location.building"
								placeholder="z.B. Gebäude A"
								@ionBlur="validateField('building')"
								:class="{ 'input-error': errors.building }"
							></ion-input>
						</ion-item>
						<div v-if="errors.building" class="error-message">
							<ion-icon :icon="alertCircleOutline"></ion-icon>
							{{ errors.building }}
						</div>
					</div>

					<!-- Floor & Room Row -->
					<div class="input-row">
						<div class="input-group half-width">
							<ion-item 
								class="modern-item" 
								:class="{ 'item-error': errors.floor, 'item-filled': location.floor }"
							>
								<ion-label position="stacked" class="custom-label">
									<ion-icon :icon="layersOutline" class="label-icon"></ion-icon>
									Etage *
								</ion-label>
								<ion-input
									v-model="location.floor"
									placeholder="z.B. 1"
									type="number"
									@ionBlur="validateField('floor')"
									:class="{ 'input-error': errors.floor }"
								></ion-input>
							</ion-item>
							<div v-if="errors.floor" class="error-message">
								<ion-icon :icon="alertCircleOutline"></ion-icon>
								{{ errors.floor }}
							</div>
						</div>

						<div class="input-group half-width">
							<ion-item 
								class="modern-item" 
								:class="{ 'item-error': errors.room, 'item-filled': location.room }"
							>
								<ion-label position="stacked" class="custom-label">
									<ion-icon :icon="homeOutline" class="label-icon"></ion-icon>
									Raum *
								</ion-label>
								<ion-input
									v-model="location.room"
									placeholder="z.B. 101"
									@ionBlur="validateField('room')"
									:class="{ 'input-error': errors.room }"
								></ion-input>
							</ion-item>
							<div v-if="errors.room" class="error-message">
								<ion-icon :icon="alertCircleOutline"></ion-icon>
								{{ errors.room }}
							</div>
						</div>
					</div>

					<!-- Action Buttons -->
					<div class="action-buttons">
						<ion-button 
							fill="outline" 
							color="danger" 
							@click="handleDelete"
							:disabled="isSaving"
							class="delete-button"
						>
							<ion-icon :icon="trashOutline" slot="start"></ion-icon>
							Standort löschen
						</ion-button>
					</div>

					<!-- Form Footer Info -->
					<div class="form-footer-info">
						<ion-item lines="none" class="info-item">
							<ion-icon :icon="informationCircleOutline" slot="start" color="primary"></ion-icon>
							<ion-label class="info-text">
								<p>Felder mit * sind Pflichtfelder</p>
							</ion-label>
						</ion-item>
					</div>
				</div>
			</div>
		</div>

		<!-- Delete Confirmation Modal -->
		<ion-alert
			:is-open="showDeleteAlert"
			header="Standort löschen"
			message="Bist du sicher, dass du diesen Standort löschen möchtest? Diese Aktion kann nicht rückgängig gemacht werden."
			:buttons="alertButtons"
			@didDismiss="showDeleteAlert = false"
		></ion-alert>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import {
	IonItem,
	IonLabel,
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
	homeOutline,
	alertCircleOutline,
	informationCircleOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
	trashOutline,
	timeOutline,
} from 'ionicons/icons';
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const location = ref({
	id: null as number | null,
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

const isLoading = ref(true);
const isSaving = ref(false);
const showDeleteAlert = ref(false);

const leftFooterButton = computed(() => ({
	name: 'Abbrechen',
	color: 'medium',
	icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
	name: isSaving.value ? 'Speichere...' : (isValid.value ? 'Änderungen speichern' : 'Felder ausfüllen'),
	color: isValid.value ? 'primary' : 'medium',
	icon: checkmarkCircleOutline,
	disabled: !isValid.value || isSaving.value,
}));

const isValid = computed(() => {
	return (
		location.value.name.trim() !== '' &&
		location.value.building.trim() !== '' &&
		location.value.floor.trim() !== '' &&
		location.value.room.trim() !== '' &&
		Object.values(errors.value).every(error => error === '')
	);
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
	return new Date(dateString).toLocaleDateString('de-DE', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	});
};

const validateField = (fieldName: keyof typeof errors.value) => {
	const value = String(location.value[fieldName as keyof typeof location.value] || '').trim();
	
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
			} else if (isNaN(Number(value))) {
				errors.value.floor = 'Etage muss eine Zahl sein';
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
};

const validateAllFields = () => {
	validateField('name');
	validateField('building');
	validateField('floor');
	validateField('room');
};

const loadLocation = async () => {
	isLoading.value = true;
	
	try {
		const locationId = route.params.id as string;
		
		// TODO: Replace with actual API call
		console.log('Loading location with ID:', locationId);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Mock data - replace with actual API data
		location.value = {
			id: Number(locationId),
			name: 'Bibliothek',
			description: 'Hauptbibliothek des Campus mit umfangreicher Sammlung',
			building: 'A',
			floor: '1',
			room: '101',
			createdAt: '2024-01-01T10:00:00Z',
			updatedAt: '2024-01-02T12:00:00Z',
		};
	} catch (error) {
		console.error('Error loading location:', error);
		// TODO: Show error toast and navigate back
		router.back();
	} finally {
		isLoading.value = false;
	}
};

const handleCancel = () => {
	router.back();
};

const handleSave = async () => {
	if (!isValid.value) {
		validateAllFields();
		return;
	}

	isSaving.value = true;
	
	try {
		// TODO: Implement actual save logic
		console.log('Saving location:', location.value);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1500));
		
		// Navigate back to locations
		router.push('/locations/home');
	} catch (error) {
		console.error('Error saving location:', error);
		// TODO: Show error toast
	} finally {
		isSaving.value = false;
	}
};

const handleDelete = () => {
	showDeleteAlert.value = true;
};

const confirmDelete = async () => {
	try {
		// TODO: Implement actual delete logic
		console.log('Deleting location:', location.value.id);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Navigate back to locations
		router.push('/locations/home');
	} catch (error) {
		console.error('Error deleting location:', error);
		// TODO: Show error toast
	}
};

// Watch for changes and clear errors
watch(
	() => location.value.name,
	() => {
		if (errors.value.name) errors.value.name = '';
	}
);

watch(
	() => location.value.building,
	() => {
		if (errors.value.building) errors.value.building = '';
	}
);

watch(
	() => location.value.floor,
	() => {
		if (errors.value.floor) errors.value.floor = '';
	}
);

watch(
	() => location.value.room,
	() => {
		if (errors.value.room) errors.value.room = '';
	}
);

onMounted(() => {
	loadLocation();
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

.modern-item {
	--background: var(--ion-color-light-tint);
	--border-radius: 12px;
	--padding-start: 16px;
	--padding-end: 16px;
	--padding-top: 12px;
	--padding-bottom: 12px;
	border: 2px solid var(--ion-color-light-shade);
	border-radius: 12px;
	margin-bottom: 8px;
	transition: all 0.3s ease;
}

.modern-item.item-filled {
	border-color: var(--ion-color-primary-tint);
	--background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-item.item-error {
	border-color: var(--ion-color-danger);
	--background: rgba(var(--ion-color-danger-rgb), 0.05);
}

.modern-item:focus-within {
	border-color: var(--ion-color-primary);
	box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
}

.custom-label {
	display: flex;
	align-items: center;
	gap: 8px;
	font-weight: 600;
	color: var(--ion-color-dark);
	margin-bottom: 4px;
}

.label-icon {
	font-size: 16px;
	color: var(--ion-color-primary);
}

.custom-textarea {
	min-height: 80px;
}

.textarea-item {
	--padding-bottom: 16px;
}

.input-error {
	color: var(--ion-color-danger);
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
	background: var(--ion-color-light-tint);
	border-radius: 12px;
	border-left: 4px solid var(--ion-color-primary);
}

.info-item {
	--background: transparent;
	--padding-start: 0;
	--padding-end: 0;
}

.info-text p {
	margin: 0;
	color: var(--ion-color-medium);
	font-size: 0.9em;
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
	0%, 100% { transform: translateX(0); }
	25% { transform: translateX(-5px); }
	75% { transform: translateX(5px); }
}

/* Responsive Design */
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
		flex-direction: column;
		gap: 4px;
		text-align: center;
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
