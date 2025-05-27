<template>
	<template-page
		:headline="'Gegenstand melden'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleCancel"
		@rightFooterButtonClicked="handleSubmit">
		
		<div class="form-container">
			<!-- Form Header -->
			<div class="form-header">
				<ion-icon :icon="megaphoneOutline" class="header-icon"></ion-icon>
				<h2>Gegenstand melden</h2>
				<p>Hast du etwas verloren oder gefunden? Melde es hier!</p>
				
				<!-- Progress Info -->
				<div class="progress-info">
					<div class="progress-bar">
						<div class="progress-fill" :style="{ width: `${completionPercentage}%` }"></div>
					</div>
					<span class="progress-text">{{ completionPercentage }}% ausgefüllt</span>
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
							placeholder="z.B. Schwarzer Rucksack, iPhone 14, Schlüsselbund..."
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
							placeholder="Beschreibe den Gegenstand so detailliert wie möglich: Farbe, Marke, besondere Merkmale, Inhalt..."
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
							placeholder="z.B. Bibliothek, Cafeteria, Sporthalle, Gebäude A..."
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
							placeholder="Was ist passiert?"
							@ionChange="validateField('status')"
							:class="{ 'input-error': errors.status }"
						>
							<ion-select-option value="lost">Ich habe etwas verloren</ion-select-option>
							<ion-select-option value="found">Ich habe etwas gefunden</ion-select-option>
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
							Bild hinzufügen (optional)
						</h3>
						<p class="upload-description">Ein Bild hilft anderen dabei, deinen Gegenstand schneller zu identifizieren</p>
						
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
								Bild hochladen
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

						<!-- Upload Tips -->
						<div class="upload-tips">
							<ion-item lines="none" class="tip-item">
								<ion-icon :icon="bulbOutline" slot="start" color="warning"></ion-icon>
								<ion-label class="tip-text">
									<p><strong>Tipp:</strong> Achte auf gute Beleuchtung und zeige charakteristische Merkmale</p>
								</ion-label>
							</ion-item>
						</div>
					</div>
				</div>

				<!-- Form Footer Info -->
				<div class="form-footer-info">
					<ion-item lines="none" class="info-item">
						<ion-icon :icon="informationCircleOutline" slot="start" color="primary"></ion-icon>
						<ion-label class="info-text">
							<p><strong>Tipp:</strong> Je genauer deine Beschreibung, desto schneller kann dein Gegenstand gefunden werden!</p>
							<p class="required-note">Felder mit * sind Pflichtfelder</p>
						</ion-label>
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

const router = useRouter();

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

const isSubmitting = ref(false);
const imagePreview = ref('');
const fileInput = ref<HTMLInputElement>();

const leftFooterButton = computed(() => ({
	name: 'Abbrechen',
	color: 'medium',
	icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
	name: isSubmitting.value ? 'Wird gemeldet...' : (isValid.value ? 'Gegenstand melden' : 'Felder ausfüllen'),
	color: isValid.value ? 'primary' : 'medium',
	icon: checkmarkCircleOutline,
	disabled: !isValid.value || isSubmitting.value,
}));

const isValid = computed(() => {
	return (
		item.value.name.trim() !== '' &&
		item.value.location.trim() !== '' &&
		item.value.status.trim() !== '' &&
		Object.values(errors.value).every(error => error === '')
	);
});

const completionPercentage = computed(() => {
	const fields = ['name', 'description', 'location', 'status'];
	const filledFields = fields.filter(field => 
		item.value[field as keyof typeof item.value].trim() !== ''
	).length;
	return Math.round((filledFields / fields.length) * 100);
});

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

const handleCancel = () => {
	router.back();
};

const handleSubmit = async () => {
	if (!isValid.value) {
		validateAllFields();
		return;
	}

	isSubmitting.value = true;
	
	try {
		// TODO: Implement actual submission logic
		console.log('Submitting item:', item.value);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 2000));
		
		// Navigate back to items
		router.push('/items/home');
	} catch (error) {
		console.error('Error submitting item:', error);
		// TODO: Show error toast
	} finally {
		isSubmitting.value = false;
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
			// TODO: Show error toast
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
		// TODO: Implement camera functionality with Capacitor
		console.log('Camera functionality not yet implemented');
		// For now, trigger file input as fallback
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
</script>

<style scoped>
.form-container {
	padding: 20px;
	max-width: 600px;
	margin: 0 auto;
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

.progress-info {
	background: var(--ion-color-light-tint);
	padding: 16px;
	border-radius: 12px;
	border: 1px solid var(--ion-color-light-shade);
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
	background: linear-gradient(90deg, var(--ion-color-primary), var(--ion-color-primary-tint));
	border-radius: 4px;
	transition: width 0.3s ease;
}

.progress-text {
	font-size: 0.85em;
	color: var(--ion-color-medium);
	font-weight: 600;
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
	background: linear-gradient(135deg, var(--ion-color-light-tint), var(--ion-color-light));
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
	border-left: 4px solid var(--ion-color-warning);
}

.tip-item {
	--background: transparent;
	--padding-start: 0;
	--padding-end: 0;
}

.tip-text p {
	margin: 0;
	color: var(--ion-color-medium-shade);
	font-size: 0.85em;
	line-height: 1.4;
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
		font-size: 48px;
	}
	
	.form-header h2 {
		font-size: 1.6em;
	}
	
	.progress-info {
		padding: 12px;
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
		padding: 16px;
	}
}
</style>
