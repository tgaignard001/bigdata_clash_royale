import { getAllLines, getNGramSummaries } from "~/composables/ngram";

export default defineEventHandler((event) => {
    const params = event.context.params?.ngram;

    if (!params) return {content: "No NGram"}
    const data = getNGramSummaries(params);
    console.log("call for ngram: ", params);

    return {
        content: data
    }
})