<template>
  <div class="container mx-auto p-4 relative overflow-hidden">
    <h1 class="text-3xl font-bold mb-6 text-center bg-clip-text text-transparent bg-gradient-to-r from-blue-500 to-purple-600">
      Processing Invoice
    </h1>
    
    <div class="max-w-md mx-auto bg-white rounded-xl border border-gray-200 shadow-lg p-8">
      <div class="flex flex-col items-center">
        <AnimatedCircularProgressBar
          :progress="progress"
          :error="!!error"
          class="mb-6"
        />
        
        <p v-if="error" class="text-lg font-medium mb-2 text-red-500">Error</p>
        <p v-else class="text-lg font-medium mb-2 text-center text-gray-800">
          Processing your document...
        </p>
        
        <p class="text-sm text-center" :class="error ? 'text-red-400' : 'text-gray-500'">
          {{ error || statusMessage }}
        </p>
      </div>
    </div>

    <Meteors :count="5" class="absolute inset-0 pointer-events-none" />
  </div>
</template>

<script setup>
import { AnimatedCircularProgressBar, Meteors } from '@/components'
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/services/api'

const router = useRouter()
const route = useRoute()
const progress = ref(0)
const statusMessage = ref('Initializing processing...')
const error = ref(null)
const interval = ref(null)

const checkStatus = async () => {
  try {
    const response = await api.getProcessingStatus(route.query.jobId)
    progress.value = response.data.progressPercentage
    statusMessage.value = response.data.statusMessage
    
    if (response.data.status === 'COMPLETED') {
      clearInterval(interval.value)
      router.push({
        name: 'results',
        query: { jobId: route.query.jobId }
      })
    } else if (response.data.status === 'FAILED') {
      error.value = response.data.errorMessage || 'Processing failed'
      clearInterval(interval.value)
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to check status'
    clearInterval(interval.value)
  }
}

onMounted(() => {
  if (!route.query.jobId) {
    router.push('/')
    return
  }
  interval.value = setInterval(checkStatus, 2000)
  checkStatus() // Initial check
})

onUnmounted(() => {
  clearInterval(interval.value)
})
</script>