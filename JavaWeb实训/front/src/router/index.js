import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component: ()=>import("@/views/LoginView.vue")
  },
  {
    path: '/index',
    name: 'index',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '@/views/ChatView.vue'),
    meta:{
      requiresAuth:true
    }
  }
]

const router = new VueRouter({
  routes
})


router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isUserLoggedIn()) {
      next({
        path: '/'
      });
    } else {
      next();
    }
  } else {
    next();
  }
});

function isUserLoggedIn() {
  let time = sessionStorage.getItem(sessionStorage.getItem("userName")+"login")
  // sessionStorage.setItem("test",sessionStorage.getItem("userName")+"====="+time)
  let available = (new Date().getTime()-time)/1000/60
  if(available<=30){
    return true
  }else{
    localStorage.setItem("login",-11)
    return false
  }
}

export default router