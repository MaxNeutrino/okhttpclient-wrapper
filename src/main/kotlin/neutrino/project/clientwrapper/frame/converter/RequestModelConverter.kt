package neutrino.project.clientwrapper.frame.converter

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.content.Content

/**
 * Used if you want custom serialization of your object
 * By default used DefaultRequestModelConverter
 * @see DefaultRequestModelConverter
 */
interface RequestModelConverter {
	fun convert(content: Content): Params
}