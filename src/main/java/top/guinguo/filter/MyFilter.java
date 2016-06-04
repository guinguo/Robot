package top.guinguo.filter;

import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by guin_guo on 2016/6/4.
 */
public class MyFilter extends CharacterEncodingFilter {
    private String encoding;

    private boolean forceEncoding = false;


    /**
     * Set the encoding to use for requests. This encoding will be passed into a
     * {@link javax.servlet.http.HttpServletRequest#setCharacterEncoding} call.
     * <p>Whether this encoding will override existing request encodings
     * (and whether it will be applied as default response encoding as well)
     * depends on the {@link #setForceEncoding "forceEncoding"} flag.
     */
    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Set whether the configured {@link #setEncoding encoding} of this filter
     * is supposed to override existing request and response encodings.
     * <p>Default is "false", i.e. do not modify the encoding if
     * {@link javax.servlet.http.HttpServletRequest#getCharacterEncoding()}
     * returns a non-null value. Switch this to "true" to enforce the specified
     * encoding in any case, applying it as default response encoding as well.
     */
    @Override
    public void setForceEncoding(boolean forceEncoding) {
        this.forceEncoding = forceEncoding;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        System.out.println(encoding+"=============encoding");
        System.out.println(forceEncoding+"=============forceEncoding");
        if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
            request.setCharacterEncoding(this.encoding);
            if (this.forceEncoding) {
                response.setCharacterEncoding(this.encoding);
            }
        }
        Enumeration<String> aa =  request.getParameterNames();
        while (aa.hasMoreElements()) {
            System.out.println("--------------"+request.getParameter(aa.nextElement()));
        }
        filterChain.doFilter(request, response);
    }
}
