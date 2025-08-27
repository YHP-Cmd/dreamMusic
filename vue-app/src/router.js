//(1)引入路由依赖
import {createRouter, createWebHashHistory} from 'vue-router'
//(2)配置组件访问的路径
const routes=[
    {
        path: '/layout',
        component: () => import('./layouts/Layout.vue'),
        children: [
            {
                path: '/home1',
                name: 'home1',
                component: () => import('./views/home/home.vue'),
                meta: { breadcrumb: ['首页'] }
            },
            {
                path: '/admin/user',
                name: 'userMessage',
                component: () => import('./views/admin/userMessage.vue'),
                meta: { breadcrumb: ['管理中心', '用户管理'] }
            },
            {
                path: '/admin/song',
                name: 'songMessage',
                component: () => import('./views/admin/songMessage.vue'),
                meta: { breadcrumb: ['管理中心', '歌曲管理'] }
            },
            {
                path: '/admin/singer',
                name: 'singerMessage',
                component: () => import('./views/admin/singerMessage.vue'),
                meta: { breadcrumb: ['管理中心', '歌手管理'] }
            },
            {
                path: '/settings/profile',
                name: 'profileSettings',
                component: () => import('./views/admin/profileSettings.vue'),
                meta: { breadcrumb: ['设置中心', '个人设置'] }
            },
            {
                path: '/settings/security',
                name: 'securitySettings',
                component: () => import('./views/admin/securitySettings.vue'),
                meta: { breadcrumb: ['设置中心', '安全设置'] }
            },
            {
                path: '/type/userType',
                name: 'userTypeMessage',
                component: () => import('./views/admin/userTypeMessage.vue'),
                meta: { breadcrumb: ['设置中心', '用户类型管理'] }
            },
            {
                path: '/type/songType',
                name: 'songTypeMessage',
                component: () => import('./views/admin/songTypeMessage.vue'),
                meta: { breadcrumb: ['设置中心', '歌曲类型管理'] }
            },
            {
                path: '/type/singerType',
                name: 'singerTypeMessage',
                component: () => import('./views/admin/singerTypeMessage.vue'),
                meta: { breadcrumb: ['设置中心', '歌手类型管理'] }
            },
            {
                path: '/permission',
                name: 'permissionMessage',
                component: () => import('./views/admin/permissionMessage.vue'),
                meta: { breadcrumb: ['设置中心', '权限管理'] }
            }
        ]
    },
    {
        path:'/',
        component:()=>import('./auth/login.vue')
    },
    {
      path: '/regis',
      component:()=>import('./auth/register.vue')
    },
    {
        path: '/temp',
        component:()=>import('./temp.vue')
    },
    {
        path: '/test',
        component:()=>import('./test.vue')
    },
    {
        path: '/home',
        component:()=>import('./layouts/home.vue'),
        children:[
            {
              path: '/comment',
              component:()=>import('./components/comment.vue')
            },
            {
                path:'/recommend',
                component:()=>import('./components/recommend.vue')
            },
            {
                path:'/my',
                component:()=>import('./components/my.vue')
            },
            {
                path:'/record',
                component:()=>import('./info/record.vue')
            },
            {
                path:'/myfaovrites',
                component:()=>import('./info/myfaovrites.vue')
            },
            {
                path:'/playlist',
                component:()=>import('./components/playlist.vue')
            },
            {
                path: '/playlistInfo',
                component:()=>import('./info/playlistInfo.vue')
            },
            {
                path:'/ranking',
                component:()=>import('./components/ranking.vue'),
                children: [
                    {
                        path: '/sumRank1',
                        component:()=>import('./info/sumRank.vue')
                    },
                    {
                        path: '/statRank',
                        component:()=>import('./info/statRank.vue')
                    },
                ]
            },
            {
                path:'/select',
                component:()=>import('./components/select.vue')
            },
            {
                path:'/singer',
                component:()=>import('./components/singer.vue')
            },
            {
                path:'/singerInfo',
                component:()=>import('./info/singerInfo.vue'),
                children:[
                    {
                        path:'/songInfo',
                        component:()=>import('./info/songInfo.vue')
                    },
                    {
                        path: '/album',
                        component:()=>import('./info/album.vue')
                    },
                    {
                        path: '/albumInfo',
                        component:()=>import('./info/albumInfo.vue')
                    }
                ]
            }
        ]
    }


]
//(3)创建路由实例
const router = createRouter({
    history: createWebHashHistory(),
    routes
})
//(4)将路由实例导出
export default router;
