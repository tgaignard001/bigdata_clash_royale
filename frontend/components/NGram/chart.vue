<script setup lang="ts">
import { Line } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, PointElement, LineElement, CategoryScale, LinearScale } from 'chart.js'
import type { ApiBody } from '~/models/api';
import type { NGramSummary } from '~/models/nGramSummary';

ChartJS.register(Title, Tooltip, Legend, PointElement, LineElement, CategoryScale, LinearScale)

const props = defineProps<{ngram: string }>();

const { data } = await useFetch<ApiBody>(`/api/ngram/${props.ngram}`)


const summaries: NGramSummary[] = (data.value) ? data.value.content : [];

const chartData = {
  labels: extractLabels(summaries),
  datasets: [
    {
      label: props.ngram,
      backgroundColor: '#f87979',
      data: extractData(summaries),
    },
  ],
};

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
};

function extractLabels(list: NGramSummary[]){
    let tmp: string[] = [];
    for (let i in list){
        const year = list[i]._1.split('-')[1].slice(1);
        const week = list[i]._1.split('-')[2];
        tmp.push(year + "-week:" + week);
    }
    return tmp;
}

function extractData(list: NGramSummary[]){
    let tmp: number[] = [];
    for (let i in list){
        tmp.push(list[i]._2.victories/list[i]._2.uses*100);
    }
    return tmp;
}

</script>
<template>
    <div class="flex flex-col items-center w-full h-full">
        <Line
        class="rounded-xl bg-white"
        :options="chartOptions" :data="chartData" />
    </div>
</template>