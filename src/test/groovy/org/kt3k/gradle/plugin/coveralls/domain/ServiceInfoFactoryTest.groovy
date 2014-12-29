package org.kt3k.gradle.plugin.coveralls.domain

import org.junit.Test
import static org.junit.Assert.*

class ServiceInfoFactoryTest {

    @Test
    void testCreateFromEnvVarNoServiceAvaiable() {
        // test the case no service is available
        assertNull ServiceInfoFactory.createFromEnvVar([:])
    }

    @Test
    void testCreateFromEnvVarTravis() {
        // test the case of travis
        ServiceInfo serviceInfo = ServiceInfoFactory.createFromEnvVar TRAVIS: 'true', TRAVIS_JOB_ID: '12345678'

        assertEquals 'travis-ci', serviceInfo.serviceName
        assertEquals '12345678', serviceInfo.serviceJobId
        assertEquals null, serviceInfo.repoToken
    }

    @Test
    void testCreateFromEnvVarTravisNoJobId() {
        // test the case that travis == true but job_id is not available
        assertNull ServiceInfoFactory.createFromEnvVar(TRAVIS: 'true')
    }


    @Test
    void testCreateFromEnvVarTravisPro() {
        // test the case of travis-pro
        ServiceInfo serviceInfo = ServiceInfoFactory.createFromEnvVar(
                TRAVIS: 'true',
                TRAVIS_JOB_ID: '12345678',
                COVERALLS_REPO_TOKEN: 'ABCDEF',
                TRAVIS_PULL_REQUEST: '3232')

        assertEquals 'travis-pro', serviceInfo.serviceName
        assertEquals '12345678', serviceInfo.serviceJobId
        assertEquals 'ABCDEF', serviceInfo.repoToken

        assertEquals '12345678', serviceInfo.environment['travis_job_id']
        assertEquals '3232', serviceInfo.environment['travis_pull_request']
    }


    @Test
    void testCreateFromEnvVarCircleci() {
        // test the case of travis-pro
        ServiceInfo serviceInfo = ServiceInfoFactory.createFromEnvVar(
                CIRCLECI: 'true',
                CIRCLE_BUILD_NUM: '12345678',
                COVERALLS_REPO_TOKEN: 'ABCDEF',
                CIRCLE_BRANCH: 'branchX',
                CIRCLE_SHA1: '231asdfadsf424')

        assertEquals 'circleci', serviceInfo.serviceName
        assertEquals '12345678', serviceInfo.serviceNumber
        assertEquals 'ABCDEF', serviceInfo.repoToken
        assertEquals 'branchX', serviceInfo.environment['branch']
        assertEquals '231asdfadsf424', serviceInfo.environment['commit_sha']
        assertEquals '12345678', serviceInfo.environment['circleci_build_num']
    }


    @Test
    void testCreateFromEnvVarFromRepoToken() {
        // test the case of the environment other than travis with repo token
        ServiceInfo serviceInfo = ServiceInfoFactory.createFromEnvVar('COVERALLS_REPO_TOKEN': 'ABCDEF')

        assertEquals 'other', serviceInfo.serviceName
        assertEquals null, serviceInfo.serviceJobId
        assertEquals 'ABCDEF', serviceInfo.repoToken
    }

    @Test
    void testCreateFromEnvVarFromRepoToken2() {

        // "other" CI that supplies env vars (like Codeship)
        ServiceInfo serviceInfo = ServiceInfoFactory.createFromEnvVar(
                COVERALLS_REPO_TOKEN: 'ABCDEF',
                CI_NAME: 'name',
                CI_BUILD_NUMBER: 'build_number',
                CI_BUILD_URL: 'build_url',
                CI_BRANCH: 'branch',
                CI_PULL_REQUEST: 'pull_request',
        )

        assertEquals serviceInfo, new ServiceInfo(
                serviceName: 'name',
                serviceNumber: 'build_number',
                serviceBuildUrl: 'build_url',
                serviceBranch: 'branch',
                servicePullRequest: 'pull_request',
                repoToken: 'ABCDEF')
    }

}
