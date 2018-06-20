package $package$.services

import io.catbird.util.Rerunnable

trait RerunnableService[-Req, Rep] extends (Req => Rerunnable[Rep])
