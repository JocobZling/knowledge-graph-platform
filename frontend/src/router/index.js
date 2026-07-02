import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import CrudPage from '../views/CrudPage.vue'
import Graph from '../views/Graph.vue'
import Search from '../views/Search.vue'
import Timeline from '../views/Timeline.vue'
import DailyBriefList from '../views/DailyBriefList.vue'
import DailyBriefDetail from '../views/DailyBriefDetail.vue'
import DailyReports from '../views/DailyReports.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Dashboard },
    { path: '/knowledge', component: CrudPage, props: { type: 'knowledge' } },
    { path: '/issues', component: CrudPage, props: { type: 'issues' } },
    { path: '/projects', component: CrudPage, props: { type: 'projects' } },
    { path: '/prompts', component: CrudPage, props: { type: 'prompts' } },
    { path: '/graph', component: Graph },
    { path: '/timeline', component: Timeline },
    { path: '/search', component: Search },
    { path: '/daily', component: DailyBriefList },
    { path: '/daily/:id', component: DailyBriefDetail },
    { path: '/reports', redirect: '/daily' }
  ]
})
