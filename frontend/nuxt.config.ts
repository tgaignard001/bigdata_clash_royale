// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: {
    enabled: true,
    timeline: {
      enabled: true,
    },
  },

  modules: ['@nuxt/ui', '@nuxt/image'],

  ssr: false,

  typescript: {
    strict: true,
  },
});
