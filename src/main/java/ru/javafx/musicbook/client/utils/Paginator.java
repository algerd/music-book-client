
package ru.javafx.musicbook.client.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
    PageRequest page1 = new PageRequest(0, 20, Direction.ASC, "lastName", "salary");
    PageRequest page2 = new PageRequest(0, 20, new Sort(
        new Order(Direction.ASC, "lastName"), 
        new Order(Direction.DESC, "salary")
    );
    После первого запроса необходимо передать пейджеру общщее количество элементов ресурса:
      setTotalElements(long)
 */
public class Paginator {
    
    private long totalElements;
    private long totalPages;
    private long page;
	private long size;
    private Sort sort;
       
    public Paginator(int size) {
        this(0, size, null);
    }
    
    /**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param sort can be {@literal null}.
	 */
	public Paginator(int page, int size, Sort sort) {
		if (page < 0) {
			throw new IllegalArgumentException("Page index must not be less than zero!");
		}
		if (size < 1) {
			throw new IllegalArgumentException("Page size must not be less than one!");
		}
		this.page = page;
		this.size = size;
		this.sort = sort;
	}
    
    /**
	 * Creates a new {@link PageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return the first page.
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 */
	public Paginator(int page, int size) {
		this(page, size, null);
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param direction the direction of the {@link Sort} to be specified, can be {@literal null}.
	 * @param properties the properties to sort by, must not be {@literal null} or empty.
	 */
	public Paginator(int page, int size, Sort.Direction direction, String... properties) {
		this(page, size, new Sort(direction, properties));
	}
    
    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        
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
    
    public long next() {
		return (hasNext()) ? ++page : page;
	}

	public long previous() {
		return page == 0 ? 0 : --page;
	}

	public long first() {
		return page = 0;
	}
    
    public long last() {
		return page = (totalPages > 0 ? totalPages - 1 : 0);                
	}
    
	public long getOffset() {
		return page * size;
	}
    
    public boolean hasNext() {
        return totalPages > 0 && totalPages - 1 > page;
	}

	public boolean hasPrevious() {
		return page > 0;
	}

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        setTotalPages();
    }
    
    private void setTotalPages() {
        this.totalPages = (size == 0 ? 0 : (long) Math.ceil((double) totalElements / (double) size));
    }
    
    public long getTotalPages() {
        return totalPages;
    }
   
    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        if (page < 0) {
			throw new IllegalArgumentException("Page index must not be less than zero!");
		}
        this.page = (page > 0 && page * size > totalElements) ? last() : page;
    }

    public long getSize() {        
        return size;
    }

    public void setSize(int size) {
        if (size < 1) {
			throw new IllegalArgumentException("Page size must not be less than one!");
		}
        this.size = size;
        this.page = 0;
        setTotalPages();    
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.page ^ (this.page >>> 32));
        hash = 83 * hash + (int) (this.size ^ (this.size >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Paginator other = (Paginator) obj;
        if (this.page != other.page) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        return true;
    }
   
}
