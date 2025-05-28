<template>
	<template-page
		:headline="'Gegenstand melden'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleCancel"
		@rightFooterButtonClicked="handleSubmit">
		<div class="form-container">
			<!-- Progress Indicator -->
			<div class="progress-section">
				<div class="progress-bar">
					<div
						class="progress-fill"
						:style="{ width: `${completionPercentage}%` }"></div>
				</div>
				<p class="progress-text">{{ completionPercentage }}% ausgefüllt</p>
			</div>

			<!-- Form Header -->
			<div class="form-header">
				<ion-icon :icon="megaphoneOutline" class="header-icon"></ion-icon>
				<h2>Gegenstand melden</h2>
				<p>
					Hilf anderen dabei, ihre verlorenen Gegenstände wiederzufinden oder
					melde einen Fund
				</p>
			</div>

			<!-- Form Content -->
			<div class="form-content">
				<!-- Name Field -->
				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-error': errors.name,
							'item-filled': item.name,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="textOutline" class="label-icon"></ion-icon>
							Name des Gegenstands *
						</ion-label>
						<ion-input
							v-model="item.name"
							placeholder="z.B. Schwarzer Rucksack, iPhone 13, Schlüsselbund"
							@ionBlur="validateField('name')"
							:class="{ 'input-error': errors.name }"></ion-input>
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
						:class="{ 'item-filled': item.description }">
						<ion-label position="stacked" class="custom-label">
							<ion-icon
								:icon="documentTextOutline"
								class="label-icon"></ion-icon>
							Beschreibung
						</ion-label>
						<ion-textarea
							v-model="item.description"
							placeholder="Detaillierte Beschreibung: Farbe, Größe, Marke, besondere Merkmale..."
							class="custom-textarea"
							:auto-grow="true"></ion-textarea>
					</ion-item>
				</div>

				<!-- Location Field -->
				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-error': errors.location,
							'item-filled': item.location,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="locationOutline" class="label-icon"></ion-icon>
							Fundort/Verlustort *
						</ion-label>
						<ion-input
							v-model="item.location"
							placeholder="z.B. Bibliothek, Hörsaal A1, Mensa, Parkplatz"
							@ionBlur="validateField('location')"
							:class="{ 'input-error': errors.location }"></ion-input>
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
						:class="{
							'item-error': errors.status,
							'item-filled': item.status,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="flagOutline" class="label-icon"></ion-icon>
							Status *
						</ion-label>
						<ion-select
							v-model="item.status"
							placeholder="Wähle den Status"
							@ionChange="validateField('status')">
							<ion-select-option value="LOST">Verloren</ion-select-option>
							<ion-select-option value="FOUND">Gefunden</ion-select-option>
						</ion-select>
					</ion-item>
					<div v-if="errors.status" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.status }}
					</div>
				</div>

				<!-- Image Upload Section -->
				<div class="input-group">
					<div class="image-upload-section">
						<h4 class="upload-title">
							<ion-icon :icon="cameraOutline" class="title-icon"></ion-icon>
							Bild hinzufügen (optional)
						</h4>
						<p class="upload-description">
							Ein Bild hilft anderen dabei, den Gegenstand besser zu
							identifizieren.
						</p>

						<!-- Image Preview -->
						<div v-if="imagePreview" class="image-preview-container">
							<img :src="imagePreview" :alt="item.name" class="image-preview" />
							<ion-button
								fill="clear"
								color="danger"
								class="remove-image-btn"
								@click="removeImage">
								<ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
							</ion-button>
						</div>

						<!-- Upload Buttons -->
						<div class="upload-buttons">
							<ion-button fill="outline" class="upload-btn" @click="takePhoto">
								<ion-icon :icon="cameraOutline" slot="start"></ion-icon>
								Foto aufnehmen
							</ion-button>
							<ion-button
								fill="outline"
								class="upload-btn"
								@click="triggerFileInput">
								<ion-icon :icon="cloudUploadOutline" slot="start"></ion-icon>
								Datei hochladen
							</ion-button>
						</div>

						<!-- Hidden file input -->
						<input
							ref="fileInput"
							type="file"
							accept="image/*"
							style="display: none"
							@change="handleFileSelect" />

						<!-- Upload Tips -->
						<div class="upload-tips">
							<ion-item class="tip-item">
								<ion-icon
									:icon="bulbOutline"
									slot="start"
									color="warning"></ion-icon>
								<div class="tip-text">
									<p>
										<strong>Tipp:</strong> Gute Bilder erhöhen die Chance, dass
										dein Gegenstand gefunden wird!
									</p>
									<p>• Verwende gute Beleuchtung</p>
									<p>• Zeige charakteristische Details</p>
									<p>• Maximal 5MB Dateigröße</p>
								</div>
							</ion-item>
						</div>
					</div>
				</div>

				<!-- Form Footer Info -->
				<div class="form-footer-info">
					<ion-item class="info-item">
						<ion-icon
							:icon="informationCircleOutline"
							slot="start"
							color="primary"></ion-icon>
						<div class="info-text">
							<p>
								Deine Meldung wird sofort veröffentlicht und ist für alle Nutzer
								sichtbar.
							</p>
							<p>Du kannst sie jederzeit bearbeiten oder löschen.</p>
							<p class="required-note">
								* Pflichtfelder müssen ausgefüllt werden.
							</p>
						</div>
					</ion-item>
				</div>
			</div>
		</div>
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
	IonSelect,
	IonSelectOption,
	IonButton,
} from '@ionic/vue';
import {
	megaphoneOutline,
	textOutline,
	documentTextOutline,
	locationOutline,
	flagOutline,
	alertCircleOutline,
	informationCircleOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
	cameraOutline,
	cloudUploadOutline,
	trashOutline,
	bulbOutline,
} from 'ionicons/icons';
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import type { Item } from '@/models/item';

const router = useRouter();
const itemStore = useItemStore();

const item = ref({
	name: '',
	description: '',
	location: '',
	status: '',
	imageData: '',
});

const errors = ref({
	name: '',
	location: '',
	status: '',
});

const isSubmitting = computed(() => itemStore.isLoading);
const imagePreview = ref('');
const fileInput = ref<HTMLInputElement>();

const leftFooterButton = computed(() => ({
	name: 'Abbrechen',
	color: 'medium',
	icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
	name: isSubmitting.value
		? 'Wird gemeldet...'
		: isValid.value
		? 'Gegenstand melden'
		: 'Felder ausfüllen',
	color: isValid.value ? 'primary' : 'medium',
	icon: checkmarkCircleOutline,
	disabled: !isValid.value || isSubmitting.value,
}));

const isValid = computed(() => {
	return (
		item.value.name.trim() !== '' &&
		item.value.location.trim() !== '' &&
		item.value.status.trim() !== '' &&
		Object.values(errors.value).every((error) => error === '')
	);
});

const completionPercentage = computed(() => {
	const fields = ['name', 'description', 'location', 'status'];
	const filledFields = fields.filter(
		(field) => item.value[field as keyof typeof item.value].trim() !== ''
	).length;
	return Math.round((filledFields / fields.length) * 100);
});

const validateField = (fieldName: keyof typeof errors.value) => {
	const value = String(
		item.value[fieldName as keyof typeof item.value] || ''
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

const handleCancel = () => {
	router.back();
};

const handleSubmit = async () => {
	if (!isValid.value) {
		validateAllFields();
		return;
	}

	try {
		// Prepare the item data for creation
		const itemData: Omit<Item, 'id' | 'createdAt' | 'updatedAt'> = {
			name: item.value.name.trim(),
			description: item.value.description.trim(),
			location: item.value.location.trim(),
			status: item.value.status,
			imageUrl: '', // Will be set by backend if image is uploaded
		};

		// Add image data if available
		if (imagePreview.value) {
			itemData.imageData = imagePreview.value;
		}

		// Create the item via the store
		const newItem = await itemStore.createItem(itemData);

		if (newItem) {
			// Navigate to the newly created item's details page
			router.push(`/items/${newItem.id}`);
		} else {
			throw new Error('Failed to create item');
		}
	} catch (error) {
		console.error('Error creating item:', error);
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
		// Validate file type
		if (!file.type.startsWith('image/')) {
			// TODO: Show error toast
			console.error('Please select an image file');
			return;
		}

		// Validate file size (max 5MB)
		const maxSize = 5 * 1024 * 1024; // 5MB
		if (file.size > maxSize) {
			// TODO: Show error toast
			console.error('File size must be less than 5MB');
			return;
		}

		const reader = new FileReader();
		reader.onload = (e) => {
			imagePreview.value = e.target?.result as string;
			item.value.imageData = imagePreview.value;
		};
		reader.readAsDataURL(file);
	}
};

const takePhoto = async () => {
	try {
		// For now, just trigger file input
		// In a real mobile app, you could use Capacitor Camera plugin
		triggerFileInput();
	} catch (error) {
		console.error('Error taking photo:', error);
		// TODO: Show error toast
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
</script>

<style scoped>
.form-container {
	padding: 20px;
	max-width: 600px;
	margin: 0 auto;
}

.progress-section {
	margin-bottom: 30px;
	text-align: center;
}

.progress-bar {
	width: 100%;
	height: 8px;
	background: var(--ion-color-light-shade);
	border-radius: 4px;
	overflow: hidden;
	margin-bottom: 8px;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(
		90deg,
		var(--ion-color-primary),
		var(--ion-color-primary-tint)
	);
	border-radius: 4px;
	transition: width 0.3s ease;
}

.progress-text {
	font-size: 0.85em;
	color: var(--ion-color-medium);
	font-weight: 600;
}

.form-header {
	text-align: center;
	margin-bottom: 40px;
}

.header-icon {
	font-size: 56px;
	color: var(--ion-color-primary);
	margin-bottom: 16px;
}

.form-header h2 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 700;
	font-size: 1.8em;
}

.form-header p {
	color: var(--ion-color-medium);
	margin: 0 0 24px 0;
	font-size: 1em;
	line-height: 1.4;
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
	transform: translateY(-2px);
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
	font-size: 18px;
	color: var(--ion-color-primary);
}

.custom-textarea {
	min-height: 80px;
	resize: vertical;
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
	--background: transparent;
	--padding-start: 0;
	--padding-end: 0;
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

.required-note {
	font-size: 0.8em !important;
	color: var(--ion-color-medium) !important;
	font-style: italic;
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

.upload-tips {
	background: rgba(var(--ion-color-warning-rgb), 0.1);
	border-radius: 12px;
	padding: 16px;
	border-left: 4px solid var(--ion-color-warning);
}

.tip-item {
	--background: transparent;
	--padding-start: 0;
	--padding-end: 0;
}

.tip-text p {
	margin: 0 0 4px 0;
	color: var(--ion-color-medium-shade);
	font-size: 0.85em;
	line-height: 1.4;
}

.tip-text p:last-child {
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

/* Responsive Design */
@media (max-width: 768px) {
	.form-container {
		padding: 16px;
	}

	.form-header {
		margin-bottom: 30px;
	}

	.header-icon {
		font-size: 48px;
	}

	.form-header h2 {
		font-size: 1.5em;
	}

	.progress-section {
		margin-bottom: 20px;
	}
}

@media (max-width: 480px) {
	.form-header {
		margin-bottom: 20px;
	}

	.input-group {
		margin-bottom: 20px;
	}

	.form-footer-info {
		margin-top: 16px;
		padding: 16px;
	}
}
</style>
