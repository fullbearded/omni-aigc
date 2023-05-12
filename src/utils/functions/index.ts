import DSDATA from '@/assets/ds.json'
import moment from 'moment'

export function getCurrentDate() {
	const date = new Date()
	const day = date.getDate()
	const month = date.getMonth() + 1
	const year = date.getFullYear()
	return `${year}-${month}-${day}`
}

export function transformData(keyword, type, replaceKey, languge = "In Chinese",) {
	const subjectList = DSDATA.topics
	const transformDataBasic = DSDATA.transform
	if (type && type == 'topic') {
		return subjectList.filter(item => item.id == keyword)[0].label
	} else if (type && type == 'time') {
		return moment(keyword).format('YYYY-MM-DD HH:mm:ss')
	} else if (type && type == 'prompt') {
		keyword = keyword.replaceAll('[PROMPT]', replaceKey)
		return keyword.replaceAll('[TARGETLANGUAGE]', languge)
	}
	return transformDataBasic.filter(item => item.k.toUpperCase() == keyword.toUpperCase())[0].v
}
