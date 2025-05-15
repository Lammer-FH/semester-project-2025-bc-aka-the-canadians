import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { IonicVue } from '@ionic/vue'
import Home from '../views/Home.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'home',
    component: Home
  },
  {
    path: '/items',
    name: 'items',
    component: () => import('../views/ItemsPage.vue')
  },
  {
    path: '/items/:id',
    name: 'item-details',
    component: () => import('../views/ItemDetailsPage.vue')
  },
  {
    path: '/items/new',
    name: 'item-create',
    component: () => import('../views/ItemCreatePage.vue')
  },
  {
    path: '/reports',
    name: 'reports',
    component: () => import('../views/ReportsPage.vue')
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/ProfilePage.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes
})

export default router 