<template>
	<template-page
		:headline="'Gegenstand bearbeiten'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleCancel"
		@rightFooterButtonClicked="handleSave">
		
		<div class="form-container">
			<!-- Loading State -->
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Gegenstand-Daten...</p>
			</div>

			<div v-else>
				<!-- Form Header -->
				<div class="form-header">
					<ion-icon :icon="createOutline" class="header-icon"></ion-icon>
					<h2>Gegenstand bearbeiten</h2>
					<p>Aktualisiere die Details für diesen Gegenstand</p>
					
					<!-- Last Modified Info -->
					<div v-if="item.updatedAt" class="last-modified">
						<ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
						<span>Zuletzt geändert: {{ formatDate(item.updatedAt) }}</span>
					</div>
				</div>

				<!-- Form Content -->
				<div class="form-content">
					<!-- Name Field -->
					<div class="input-group">
						<ion-item 
							class="modern-item" 
							:class="{ 'item-error': errors.name, 'item-filled': item.name }"
						>
							<ion-label position="stacked" class="custom-label">
								<ion-icon :icon="textOutline" class="label-icon"></ion-icon>
								Gegenstand-Name *
							</ion-label>
							<ion-input
								v-model="item.name"
								placeholder="z.B. Schwarzer Rucksack"
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
							:class="{ 'item-filled': item.description }"
						>
							<ion-label position="stacked" class="custom-label">
								<ion-icon :icon="documentTextOutline" class="label-icon"></ion-icon>
								Beschreibung
							</ion-label>
							<ion-textarea
								v-model="item.description"
								placeholder="Detaillierte Beschreibung des Gegenstands..."
								:rows="3"
								:auto-grow="true"
								class="custom-textarea"
							></ion-textarea>
						</ion-item>
					</div>

					<!-- Location Field -->
					<div class="input-group">
						<ion-item 
							class="modern-item" 
							:class="{ 'item-error': errors.location, 'item-filled': item.location }"
						>
							<ion-label position="stacked" class="custom-label">
								<ion-icon :icon="locationOutline" class="label-icon"></ion-icon>
								Standort *
							</ion-label>
							<ion-input
								v-model="item.location"
								placeholder="z.B. Bibliothek"
								@ionBlur="validateField('location')"
								:class="{ 'input-error': errors.location }"
							></ion-input>
						</ion-item>
						<div v-if="errors.location" class="error-message">
							<ion-icon :icon="alertCircleOutline"></ion-icon>
							{{ errors.location }}
						</div>
					</div>

					<!-- Status Field -->
					<div class="input-group">
						<ion-item 
							class="modern-item" 
							:class="{ 'item-error': errors.status, 'item-filled': item.status }"
						>
							<ion-label position="stacked" class="custom-label">
								<ion-icon :icon="flagOutline" class="label-icon"></ion-icon>
								Status *
							</ion-label>
							<ion-select 
								v-model="item.status" 
								placeholder="Status auswählen"
								@ionChange="validateField('status')"
								:class="{ 'input-error': errors.status }"
							>
								<ion-select-option value="lost">Verloren</ion-select-option>
								<ion-select-option value="found">Gefunden</ion-select-option>
								<ion-select-option value="claimed">Abgeholt</ion-select-option>
							</ion-select>
						</ion-item>
						<div v-if="errors.status" class="error-message">
							<ion-icon :icon="alertCircleOutline"></ion-icon>
							{{ errors.status }}
						</div>
					</div>

					<!-- Image Upload Field -->
					<div class="input-group">
						<div class="image-upload-section">
							<h3 class="upload-title">
								<ion-icon :icon="cameraOutline" class="title-icon"></ion-icon>
								Bild bearbeiten
							</h3>
							<p class="upload-description">Aktualisiere das Bild oder füge ein neues hinzu</p>
							
							<!-- Image Preview -->
							<div v-if="imagePreview" class="image-preview-container">
								<img :src="imagePreview" alt="Gegenstand Vorschau" class="image-preview" />
								<ion-button 
									fill="clear" 
									color="danger" 
									@click="removeImage"
									class="remove-image-btn"
								>
									<ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
								</ion-button>
							</div>

							<!-- Upload Buttons -->
							<div v-else class="upload-buttons">
								<input 
									ref="fileInput"
									type="file" 
									accept="image/*" 
									@change="handleFileSelect"
									style="display: none"
								/>
								<ion-button 
									fill="outline" 
									color="primary" 
									@click="triggerFileInput"
									class="upload-btn"
								>
									<ion-icon :icon="cloudUploadOutline" slot="start"></ion-icon>
									Neues Bild hochladen
								</ion-button>
								<ion-button 
									fill="outline" 
									color="secondary" 
									@click="takePhoto"
									class="upload-btn"
								>
									<ion-icon :icon="cameraOutline" slot="start"></ion-icon>
									Foto aufnehmen
								</ion-button>
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
							Gegenstand löschen
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
			header="Gegenstand löschen"
			message="Bist du sicher, dass du diesen Gegenstand löschen möchtest? Diese Aktion kann nicht rückgängig gemacht werden."
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
	IonSelect,
	IonSelectOption,
} from '@ionic/vue';
import {
	createOutline,
	textOutline,
	documentTextOutline,
	locationOutline,
	flagOutline,
	alertCircleOutline,
	informationCircleOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
	trashOutline,
	timeOutline,
	cameraOutline,
	cloudUploadOutline,
} from 'ionicons/icons';
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { Item } from '@/models/item';

const router = useRouter();
const route = useRoute();

const item = ref<Item>({
	id: 1,
	name: '',
	description: '',
	location: '',
	status: 'lost',
	createdAt: '',
	updatedAt: '',
	imageData: '',
});

const errors = ref({
	name: '',
	location: '',
	status: '',
});

const isLoading = ref(true);
const isSaving = ref(false);
const showDeleteAlert = ref(false);
const imagePreview = ref('');
const fileInput = ref<HTMLInputElement>();

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
		item.value.name.trim() !== '' &&
		item.value.location.trim() !== '' &&
		item.value.status.trim() !== '' &&
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
	const value = String(item.value[fieldName as keyof typeof item.value] || '').trim();
	
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
		case 'location':
			if (!value) {
				errors.value.location = 'Standort ist erforderlich';
			} else {
				errors.value.location = '';
			}
			break;
		case 'status':
			if (!value) {
				errors.value.status = 'Status ist erforderlich';
			} else {
				errors.value.status = '';
			}
			break;
	}
};

const validateAllFields = () => {
	validateField('name');
	validateField('location');
	validateField('status');
};

const loadItem = async () => {
	isLoading.value = true;
	
	try {
		const itemId = route.params.id as string;
		
		// TODO: Replace with actual API call
		console.log('Loading item with ID:', itemId);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Mock data - replace with actual API data
		item.value = {
			id: Number(itemId),
			name: 'Schwarzer Rucksack',
			description: 'Großer schwarzer Rucksack mit Laptop-Fach, Marke Eastpak',
			location: 'Bibliothek',
			status: 'found',
			createdAt: '2024-01-15T10:00:00Z',
			updatedAt: '2024-01-16T14:30:00Z',
			imageUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjY2NjIi8+CiAgPHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxOCIgZmlsbD0iIzMzMyIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkJlaXNwaWVsIEJpbGQ8L3RleHQ+Cjwvc3ZnPg==',
		};
		
		// Set existing image if available
		if (item.value.imageUrl) {
			imagePreview.value = item.value.imageUrl;
		}
	} catch (error) {
		console.error('Error loading item:', error);
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
		console.log('Saving item:', item.value);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1500));
		
		// Navigate back to item details
		router.push(`/items/${item.value.id}`);
	} catch (error) {
		console.error('Error saving item:', error);
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
		console.log('Deleting item:', item.value.id);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Navigate back to items
		router.push('/items/home');
	} catch (error) {
		console.error('Error deleting item:', error);
		// TODO: Show error toast
	}
};

// Image handling functions
const triggerFileInput = () => {
	fileInput.value?.click();
};

const handleFileSelect = (event: Event) => {
	const target = event.target as HTMLInputElement;
	const file = target.files?.[0];
	
	if (file) {
		if (file.size > 5 * 1024 * 1024) { // 5MB limit
			console.error('File too large. Maximum size is 5MB.');
			return;
		}
		
		const reader = new FileReader();
		reader.onload = (e) => {
			const result = e.target?.result as string;
			imagePreview.value = result;
			item.value.imageData = result;
		};
		reader.readAsDataURL(file);
	}
};

const takePhoto = async () => {
	try {
		console.log('Camera functionality not yet implemented');
		triggerFileInput();
	} catch (error) {
		console.error('Error taking photo:', error);
	}
};

const removeImage = () => {
	imagePreview.value = '';
	item.value.imageData = '';
	if (fileInput.value) {
		fileInput.value.value = '';
	}
};

// Watch for changes and clear errors
watch(
	() => item.value.name,
	() => {
		if (errors.value.name) errors.value.name = '';
	}
);

watch(
	() => item.value.location,
	() => {
		if (errors.value.location) errors.value.location = '';
	}
);

watch(
	() => item.value.status,
	() => {
		if (errors.value.status) errors.value.status = '';
	}
);

onMounted(() => {
	loadItem();
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

/* Image Upload Styles */
.image-upload-section {
	background: var(--ion-color-light-tint);
	border-radius: 16px;
	padding: 20px;
	border: 2px dashed var(--ion-color-light-shade);
	transition: all 0.3s ease;
}

.image-upload-section:hover {
	border-color: var(--ion-color-primary-tint);
	background: rgba(var(--ion-color-primary-rgb), 0.02);
}

.upload-title {
	display: flex;
	align-items: center;
	gap: 8px;
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-size: 1.1em;
	font-weight: 600;
}

.title-icon {
	font-size: 20px;
	color: var(--ion-color-primary);
}

.upload-description {
	color: var(--ion-color-medium);
	margin: 0 0 20px 0;
	font-size: 0.9em;
	line-height: 1.4;
}

.image-preview-container {
	position: relative;
	margin-bottom: 16px;
}

.image-preview {
	width: 100%;
	max-width: 300px;
	height: 200px;
	object-fit: cover;
	border-radius: 12px;
	border: 2px solid var(--ion-color-light-shade);
	display: block;
	margin: 0 auto;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.remove-image-btn {
	position: absolute;
	top: 8px;
	right: 50%;
	transform: translateX(calc(50% - 150px));
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	--padding-start: 8px;
	--padding-end: 8px;
	--padding-top: 8px;
	--padding-bottom: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.upload-buttons {
	display: flex;
	gap: 12px;
	margin-bottom: 16px;
	justify-content: center;
}

.upload-btn {
	--border-radius: 12px;
	font-weight: 600;
	flex: 1;
	max-width: 200px;
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
