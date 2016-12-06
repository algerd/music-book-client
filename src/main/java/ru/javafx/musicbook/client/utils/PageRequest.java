
package ru.javafx.musicbook.client.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.javafx.musicbook.client.utils.Sort.Direction;

/**
    PageRequest page1 = new PageRequest(0, 20, Direction.ASC, "lastName", "salary");
    PageRequest page2 = new PageRequest(0, 20, new Sort(
        new Order(Direction.ASC, "lastName"), 
        new Order(Direction.DESC, "salary")
    );
 */
public class PageRequest extends AbstractPageRequest {

	private static final long serialVersionUID = -4541509938956089562L;

	private final Sort sort;

	/**
	 * Creates a new {@link PageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return the first
	 * page.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 */
	public PageRequest(int page, int size) {
		this(page, size, null);
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param direction the direction of the {@link Sort} to be specified, can be {@literal null}.
	 * @param properties the properties to sort by, must not be {@literal null} or empty.
	 */
	public PageRequest(int page, int size, Direction direction, String... properties) {
		this(page, size, new Sort(direction, properties));
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param sort can be {@literal null}.
	 */
	public PageRequest(int page, int size, Sort sort) {
		super(page, size);
		this.sort = sort;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getSort()
	 */
	public Sort getSort() {
		return sort;
	}
    
    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", getPageNumber());
        parameters.put("size", getPageSize());
        
        if (sort != null) {
            String strSort = "";
            Iterator<Sort.Order> sortIterator = sort.iterator();
            while (sortIterator.hasNext()) {
                Sort.Order order = sortIterator.next();
                strSort += ((!strSort.equals("")) ? "&sort=" : "") 
                    + order.getProperty()
                    + "," + ((order.getDirection().equals(Sort.Direction.DESC)) ? "desc" : "asc");
            } 
            parameters.put("sort", strSort);           
        }
        return parameters;
    }

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#next()
	 */
	public AbstractPageRequest next() {
		return new PageRequest(getPageNumber() + 1, getPageSize(), getSort());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.AbstractPageRequest#previous()
	 */
	public PageRequest previous() {
		return getPageNumber() == 0 ? this : new PageRequest(getPageNumber() - 1, getPageSize(), getSort());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#first()
	 */
	public AbstractPageRequest first() {
		return new PageRequest(0, getPageSize(), getSort());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PageRequest)) {
			return false;
		}

		PageRequest that = (PageRequest) obj;

		boolean sortEqual = this.sort == null ? that.sort == null : this.sort.equals(that.sort);

		return super.equals(that) && sortEqual;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 31 * super.hashCode() + (null == sort ? 0 : sort.hashCode());
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(),
				sort == null ? null : sort.toString());
	}
}

