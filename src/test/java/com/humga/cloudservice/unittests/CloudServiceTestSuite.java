package com.humga.cloudservice.unittests;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;


@SelectPackages({"com.humga.cloudservice.unittests.servicetests",
        "com.humga.cloudservice.unittests.controllertests"})
@Suite
class CloudServiceTestSuite {
}
