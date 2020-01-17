#include "mmu.h"
#include <stdio.h>

/* I'm defining the CR3 "register" as a void*, but you are free to redefine it
 * as necessary.  Keep in mind that whatever type you use, it MUST be a
 * pointer.  However, the linker will not verify this.  If you redefine this
 * global as some non-pointer type, your program will probably crash.
 */
extern void* CR3;

/* The supervisor mode indicates that the processor is running in the operating
 * system.  If the permission bits are used, e.g., not in legacy mode, then
 * accessing a privileged page is only valid when SUPER is true.  Otherwise, it
 * is a protection fault.
 */
extern int  SUPER;


/* The page table is the same for both 16-bit and 32-bit addressing modes:
 *
 *  31    30..28 27..............................................4 3 2 1 0
 * +-----+------+-------------------------------------------------+-+-+-+-+
 * |Valid|unused| 24-bit Physical Page Number                     |P|R|W|X|
 * +-----+------+-------------------------------------------------+-+-+-+-+
 *
 * Unlike Intel, which uses a 4k (12-bit) page, this system uses a much smaller,
 * 256-byte (8-bit) page.
 */

/* 16-bit legacy mode.
 * In legacy mode, CR3 points to an array of PTE.  Since there are only 256
 * pages, this array is rather small (1k).  Legacy mode doesn't enforce
 * permissions; every page is assumed to be read-write-executed regardless of
 * the permission bits.
 */




/* In 32-bit mode, CR3 points to an array of 256 directory pointers.  The format
 * of a directory pointer is the following:
 *
 *  31......................................................4 3....1     0
 * +---------------------------------------------------------+------+-----+
 * | Address of page table directory                         |Unused|Valid|
 * +---------------------------------------------------------+------+-----+
 *
 * Note that only 24-bits are needed for the pointer.  Each directory starts on
 * a page boundary, which means the four lest significant bits are always zero.
 * This is where we hide the valid bit.
 *
 * Each directory is an array of pointers with the same format above.  These
 * pointers, however, point at page tables.  Decoding a 32-bit address looks
 * like this:
 *
 *  31...........24 23..............16 15....................4 3.........0
 * +---------------+------------------+-----------------------+-----------+
 * |Directory Index| Page Table Index |Page Table Entry Index |Page Offset|
 * +---------------+------------------+-----------------------+-----------+
 *       |                  |                 |                     
 * CR3   |   Root Dir.      |   Directory     |    Page Table       
 *  |    |   +------+       |   +------+      |     +------+        
 *  |    |   |      |       |   |      |      |     |      |        
 *  |    |   |      |       |   |      |      |     |      |
 *  |    +-> |======|--+    +-> |======|--+   +---> |======|---> PPN
 *  |        |      |  |        |      |  |         |      |
 *  |        |      |  |        |      |  |         |      |
 *  |        |      |  |        |      |  |         |      |
 *  |        |      |  |        |      |  |         |      |
 *  +------> +------+  +------> +------+  +-------> +------+
 *
 * Of course, you must check the valid bit at each level.  To be clear, the
 * diagrams are "upside-down" (i.e., little address are at the bottom).  
 */

/* 32-bit mode with protection.
 */


/**************************************************************************/

/* Success */
static
result_t success(void* pa) {
	result_t res;
	res.status = SUCCESS;
	res.value.pa = pa;
	return res;
}

/*  PTE Valid */
static
int pte_valid (unsigned pte) {
	return 1 & (pte >> 31);
}

/* PageFault */
static 
result_t pageFault(unsigned vpn){
	result_t res;
	res.status = PAGEFAULT;
	res.value.vpn = vpn;
	return res;
}

/* ProtFault */
static 
result_t protFault(unsigned pa){
	result_t res;
	res.status = PROTFAULT;
	res.value.pa = pa;
	return res;
}
/* PTE PPN */
static
unsigned pte_ppn(unsigned pte){
	return (pte>>4)& 0xFFFFFF;
}


/***************************
* 16-bit Legacy Addressing *
****************************/

result_t mmu_legacy (unsigned short va){
	unsigned vpn = 0xFF & (va>>8);
	unsigned po = 0xFF & va;
	unsigned* cr3 = CR3; 

	unsigned pte = cr3[vpn];
	if(pte_valid(pte))
		return success((void*)((pte_ppn(pte))<<8) | po));
	else
		return pageFault(vpn);
}

/* Find PTE */
unsigned find_pte (unsigned short va) {
	return CR3[0xFF & (va>>8)];
}

/**************************************************************************/

result_t mmu_resolve(void* va, access_t use)
{
  result_t result = {NOTIMPLEMENTED};
  return result;
}