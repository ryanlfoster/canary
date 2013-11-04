package com.citytechinc.monitoring.extension.metaclass

import org.codehaus.groovy.runtime.InvokerHelper

import javax.jcr.Node
import javax.jcr.PropertyType
import javax.jcr.Value

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class GroovyExtensionMetaClassRegistry {

    static void registerMetaClasses() {
        registerNodeMetaClass()
    }

    static void removeMetaClasses() {
        def metaRegistry = InvokerHelper.metaRegistry

        metaRegistry.removeMetaClass(Node)
    }

    private static void registerNodeMetaClass() {
        Node.metaClass {
            iterator {
                delegate.nodes
            }

            recurse { c ->
                c(delegate)

                delegate.nodes.each { node ->
                    node.recurse(c)
                }
            }

            recurse { String primaryNodeTypeName, c ->
                if (delegate.primaryNodeType.name == primaryNodeTypeName) {
                    c(delegate)
                }

                delegate.nodes.findAll { it.primaryNodeType.name == primaryNodeTypeName }.each { node ->
                    node.recurse(primaryNodeTypeName, c)
                }
            }

            recurse { Collection<String> primaryNodeTypeNames, c ->
                if (primaryNodeTypeNames.contains(delegate.primaryNodeType.name)) {
                    c(delegate)
                }

                delegate.nodes.findAll { primaryNodeTypeNames.contains(it.primaryNodeType.name) }.each { node ->
                    node.recurse(primaryNodeTypeNames, c)
                }
            }

            get { String propertyName ->
                def result = null

                if (delegate.hasProperty(propertyName)) {
                    def property = delegate.getProperty(propertyName)

                    if (property.multiple) {
                        result = property.values.collect { getResult(it) }
                    } else {
                        result = getResult(property.value)
                    }
                }

                result
            }

            set { String propertyName, value ->
                if (value == null) {
                    if (delegate.hasProperty(propertyName)) {
                        delegate.getProperty(propertyName).remove()
                    }
                } else {
                    def valueFactory = delegate.session.valueFactory

                    if (value instanceof Object[] || value instanceof Collection) {
                        def values = value.collect { valueFactory.createValue(it) }.toArray(new Value[0])

                        delegate.setProperty(propertyName, values)
                    } else {
                        def jcrValue = valueFactory.createValue(value)

                        delegate.setProperty(propertyName, jcrValue)
                    }
                }
            }

            getOrAddNode { String name ->
                delegate.hasNode(name) ? delegate.getNode(name) : delegate.addNode(name)
            }

            getOrAddNode { String name, String primaryNodeTypeName ->
                delegate.hasNode(name) ? delegate.getNode(name) : delegate.addNode(name, primaryNodeTypeName)
            }

            removeNode { String name ->
                boolean removed = false

                if (delegate.hasNode(name)) {
                    delegate.getNode(name).remove()
                    removed = true
                }

                removed
            }
        }
    }

    private static def getResult(value) {
        def result = null

        switch (value.type) {
            case PropertyType.BINARY:
                result = value.binary
                break
            case PropertyType.BOOLEAN:
                result = value.boolean
                break
            case PropertyType.DATE:
                result = value.date
                break
            case PropertyType.DECIMAL:
                result = value.decimal
                break
            case PropertyType.DOUBLE:
                result = value.double
                break
            case PropertyType.LONG:
                result = value.long
                break
            case PropertyType.STRING:
                result = value.string
        }

        result
    }
}