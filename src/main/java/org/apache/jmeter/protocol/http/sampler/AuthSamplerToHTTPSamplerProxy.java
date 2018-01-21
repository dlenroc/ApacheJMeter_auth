package org.apache.jmeter.protocol.http.sampler;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.Interruptible;

/**
 * Hack, duplicated org.apache.jmeter.protocol.http.sampler.AuthSamplerToHTTPSamplerProxy
 */
public class AuthSamplerToHTTPSamplerProxy extends HTTPSamplerBase implements Interruptible {

    private static final long serialVersionUID = 1L;

    private transient HTTPAbstractImpl impl;

    private transient volatile boolean notifyFirstSampleAfterLoopRestart;

    public AuthSamplerToHTTPSamplerProxy() {
        super();
    }

    /**
     * Convenience method used to initialise the implementation.
     *
     * @param impl the implementation to use.
     */
    public AuthSamplerToHTTPSamplerProxy(String impl) {
        super();
        setImplementation(impl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HTTPSampleResult sample(java.net.URL u, String method, boolean areFollowingRedirect, int depth) {
        // When Retrieve Embedded resources + Concurrent Pool is used
        // as the instance of Proxy is cloned, we end up with impl being null
        // testIterationStart will not be executed but it's not a problem for 51380 as it's download of resources
        // so SSL context is to be reused
        if (impl == null) { // Not called from multiple threads, so this is OK
            try {
                impl = HTTPSamplerFactory.getImplementation(getImplementation(), this);
            } catch (Exception ex) {
                return errorResult(ex, new HTTPSampleResult());
            }
        }
        // see https://bz.apache.org/bugzilla/show_bug.cgi?id=51380
        if (notifyFirstSampleAfterLoopRestart) {
            impl.notifyFirstSampleAfterLoopRestart();
            notifyFirstSampleAfterLoopRestart = false;
        }
        return impl.sample(u, method, areFollowingRedirect, depth);
    }

    // N.B. It's not possible to forward threadStarted() to the implementation class.
    // This is because Config items are not processed until later, and HTTPDefaults may define the implementation

    @Override
    public void threadFinished() {
        if (impl != null) {
            impl.threadFinished(); // Forward to sampler
        }
    }

    @Override
    public boolean interrupt() {
        if (impl != null) {
            return impl.interrupt(); // Forward to sampler
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase#testIterationStart(org.apache.jmeter.engine.event.LoopIterationEvent)
     */
    @Override
    public void testIterationStart(LoopIterationEvent event) {
        notifyFirstSampleAfterLoopRestart = true;
    }
}
