import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
import ItemsHomePage from '@/views/ItemPages/ItemsHomePage.vue';
import LocationsHomePage from '@/views/LocationsPages/LocationsHomePage.vue';
import ReportItemPage from '@/views/ItemPages/ReportItemPage.vue';
import ItemDetailsPage from '@/views/ItemPages/ItemDetailsPage.vue';
import EditItemPage from '@/views/ItemPages/EditItemPage.vue';
import AddLocationPage from '@/views/LocationsPages/AddLocationPage.vue';
import LocationDetailsPage from '@/views/LocationsPages/LocationDetailsPage.vue';
import EditLocationPage from '@/views/LocationsPages/EditLocationPage.vue';
import UserProfilePage from '@/views/UserPages/UserProfilePage.vue';

const routes: Array<RouteRecordRaw> = [
	{
		path: '/',
		redirect: '/items/home',
	},
	{
		path: '/items',
		redirect: '/items/home',
	},
	{
		path: '/items/home',
		name: 'items-home',
		component: ItemsHomePage,
	},
	{
		path: '/items/report',
		name: 'report-item',
		component: ReportItemPage,
	},
	{
		path: '/items/details',
		name: 'item-details',
		component: ItemDetailsPage,
	},
	{
		path: '/items/edit',
		name: 'edit-item',
		component: EditItemPage,
	},
	{
		path: '/locations',
		redirect: '/locations/home',
	},
	{
		path: '/locations/home',
		name: 'locations-home',
		component: LocationsHomePage,
	},
	{
		path: '/locations/add',
		name: 'add-location',
		component: AddLocationPage,
	},
	{
		path: '/locations/details',
		name: 'location-details',
		component: LocationDetailsPage,
	},
	{
		path: '/locations/edit',
		name: 'edit-location',
		component: EditLocationPage,
	},
	{
		path: '/user',
		name: 'user-profile',
		component: UserProfilePage,
	},
];

const router = createRouter({
	history: createWebHashHistory(import.meta.env.BASE_URL),
	routes,
});

export default router;
