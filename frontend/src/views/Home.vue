<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Campus Lost & Found</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">Campus Lost & Found</ion-title>
        </ion-toolbar>
      </ion-header>

      <ion-list>
        <ion-item v-for="item in items" :key="item.id" @click="navigateToItem(item.id)">
          <ion-label>
            <h2>{{ item.name }}</h2>
            <p>{{ item.description }}</p>
          </ion-label>
        </ion-item>
      </ion-list>

      <ion-fab vertical="bottom" horizontal="end" slot="fixed">
        <ion-fab-button @click="navigateToCreate">
          <ion-icon :icon="add"></ion-icon>
        </ion-fab-button>
      </ion-fab>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem, IonLabel, IonFab, IonFabButton, IonIcon } from '@ionic/vue';
import { add } from 'ionicons/icons';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import { Item } from '@/models/item';

const router = useRouter();
const itemStore = useItemStore();
const items = ref<Item[]>([]);

onMounted(async () => {
  await itemStore.fetchItems();
  items.value = itemStore.getItems;
});

const navigateToItem = (id: number) => {
  router.push({ name: 'item-details', params: { id } });
};

const navigateToCreate = () => {
  router.push({ name: 'item-create' });
};
</script>

<style scoped>
ion-content {
  --padding-top: 16px;
  --padding-bottom: 16px;
}

ion-item {
  --padding-start: 16px;
  --padding-end: 16px;
  --padding-top: 8px;
  --padding-bottom: 8px;
}
</style> 