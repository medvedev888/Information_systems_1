import { defineStore } from 'pinia'
import api from '@/axios.js'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null
  }),

  actions: {
    async fetchMe() {
      try {
        const res = await api.get('/auth/me')
        this.user = res.data
      } catch {
        this.user = null
      }
    }
  }
})
